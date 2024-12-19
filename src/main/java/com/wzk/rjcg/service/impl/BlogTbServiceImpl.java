package com.wzk.rjcg.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzk.rjcg.dto.ScrollResult;
import com.wzk.rjcg.dto.UserDTO;
import com.wzk.rjcg.entity.Follow;
import com.wzk.rjcg.entity.UserTb;
import com.wzk.rjcg.mapper.BlogTbMapper;
import com.wzk.rjcg.entity.Blog;
import com.wzk.rjcg.service.BlogTbService;
import com.wzk.rjcg.service.IFollowService;
import com.wzk.rjcg.service.UserTbService;
import com.wzk.rjcg.util.RedisConstants;
import com.wzk.rjcg.util.Result;
import com.wzk.rjcg.util.SystemConstants;
import com.wzk.rjcg.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.wzk.rjcg.util.RedisConstants.FEED_KEY;

/**
 * 博客;(BlogTb)表服务实现类
 *
 * @author makejava
 * @since 2024-12-13 14:07:08
 */
@Service
@Slf4j
public class BlogTbServiceImpl extends ServiceImpl<BlogTbMapper, Blog> implements BlogTbService {
	
	@Resource
	public UserTbService userTbService;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private UserTbService userService;
	@Resource
	private IFollowService followService;
	
	@Override
	public Result queryHotBlog(Integer current) {
		// 根据用户查询
		Page<Blog> page = query()
				.orderByDesc("liked")
				.page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
		// 获取当前页数据
		List<Blog> records = page.getRecords();
		// 查询用户
		records.forEach(blog -> {
			queryBlogUser(blog);//博客显示的用户名和头像
			isLiked(blog);
		});
		//判断是点过赞，给isLike赋值
		return Result.ok(records);
	}
	
	@Override
	public Result queryBlogById(Long id) {
		//查询博客
		Blog blog = getById(id);
		if (blog == null) {
			return Result.fail("博客不存在！");
		}
		//查询用户
		queryBlogUser(blog);
		//判断是点过赞，给isLike赋值
		isLiked(blog);
		log.info("查询到的博客：{}", blog);
		return Result.ok(blog);
	}
	
	@Override
	public Result saveBlog(Blog blog) {
		//1.获取登录用户(作者)
		UserDTO user = UserHolder.getUser();
		blog.setUserId(user.getId());
		blog.setCreateTime(new Date());
		blog.setLiked(0);
		if(blog.getShopId() == null){
			blog.setShopId(0);
		}
		//2.保存探店博文
		boolean isSuccess = save(blog);
		if(!isSuccess){
			return Result.fail("新增探店博文失败！");
		}
		//3.查询作者粉丝  select * from tb_follow where follow_user_id = ?
		List<Follow> follows = followService.query().eq("follow_user_id", user.getId()).list();
		//4..将消息推送到每个粉丝
		for (Follow follow : follows) {
			//4.1获取粉丝id
			Integer userId = follow.getUserId();
			String key = FEED_KEY + userId;
			//4.2推送
			stringRedisTemplate.opsForZSet().add(key,blog.getId().toString(),System.currentTimeMillis());
		}
		//5.返回id
		return Result.ok(blog.getId());
	}
	
	@Override
	public Result likeBlog(Integer id) {
		//1.获取登录用户id
		Integer userId = UserHolder.getUser().getId();
		String key = RedisConstants.BLOG_LIKED_KEY + id;
		//2.判断当前用户是否点赞
		Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
		if(score == null){
			//3.未点赞，点赞
			//3.没点过，点赞数加一
			boolean success = update().setSql("liked = liked + 1").eq("id", id).update();
			//保存到redis的set
			if (success) {
				stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
			}
		}else {
			//4.点过，点赞数减一
			boolean success = update().setSql("liked = liked - 1").eq("id", id).update();
			//从redis的set移除
			if (success) {
				stringRedisTemplate.opsForZSet().remove(key, userId.toString());
			}
		}
		return Result.ok();
	}
	
	@Override
	public Result queryBlogLikes(Integer id) {
		//查询top5的点赞用户
		String key = RedisConstants.BLOG_LIKED_KEY + id;
		//利用redis的zset获取top5
		Set<String> top5 = stringRedisTemplate.opsForZSet().range(key, 0, 4);
		if (top5 == null || top5.isEmpty()) {
			return Result.ok(Collections.emptyList());
		}
		//解析用户id
		List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
		String join = StrUtil.join(",", ids);
		//根据id查询用户 WHERE id IN ( 5 , 1 ) ORDER BY FIELD(id, 5, 1)
		List<UserDTO> userDTOList = userService.query()
				.in("id", ids).last("ORDER BY FIELD(id," + join + ")").list()
				.stream()
				.map(user -> BeanUtil.copyProperties(user, UserDTO.class))
				.collect(Collectors.toList());
		log.info("博客点赞列表userDTOList:{}",userDTOList);
		return Result.ok(userDTOList);
	}
	
	@Override
	public Result queryBlogsOfFollow(Long max, Integer offset) {
		//1.获取当前用户
		Integer userId = UserHolder.getUser().getId();
		//2.查询收件箱 ZREVRANGEBYSCORE key Max Min LIMIT offset count
		String key = FEED_KEY + userId;
		/*
			stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores：
				使用Redis的有序集合操作，按分数（时间戳）倒序查询指定范围内的元素，并返回元素及其分数。
			reverseRangeByScoreWithScores(key, 0, max, offset, 2)：
				从key对应的有序集合中，查询分数在0到max之间的元素，从offset开始，每次返回2个元素。
		 */
		Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, max, offset, 4);
		//3.解析数据  blogid mintime offset
		if (tuples == null || tuples.isEmpty()) {
			return Result.ok();
		}
		List<Long> ids = new ArrayList<>(tuples.size());
		long minTime = 0;//记录当前最小的时间戳。
		int os = 1;//记录当前时间戳下的偏移量。
		for (ZSetOperations.TypedTuple<String> tuple : tuples) {
			//3.1 获取id
			ids.add(Long.valueOf(tuple.getValue()));
			//3.2获取分数(时间戳)
			long time = tuple.getScore().longValue();
			if(minTime == time){
				os++;
			}else{
				minTime = time;
				os = 1;
			}
		}//将博客ID和分数（时间戳）分别存入ids和minTime，并更新偏移量os。
		//4.根据id查询blog
		String idStr = StrUtil.join(",", ids);
		List<Blog> blogs = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
		for (Blog blog : blogs) {
			//查询用户
			queryBlogUser(blog);
			//判断是点过赞，给isLike赋值
			isLiked(blog);
		}
		//5.封装返回
		ScrollResult scrollResult = new ScrollResult();
		scrollResult.setList(blogs);
		scrollResult.setMinTime(minTime);
		scrollResult.setOffset(os);
		return Result.ok(scrollResult);
	}
	
	private void queryBlogUser(Blog blog) {
		Integer userId = blog.getUserId();
		UserTb user = userTbService.getById(userId);
		blog.setName(user.getName());
		blog.setIcon(user.getIcon());
	}
	private void isLiked(Blog blog) {
		//1. 获取登录用户id
		UserDTO user = UserHolder.getUser();
		//用户没登录就不需要检查是否点赞
		if (user == null) {
			return;
		}
		Integer userID = user.getId();
		String key = "blog:liked:" + blog.getId();
		//2.查询用户是否点赞
		Double score = stringRedisTemplate.opsForZSet().score(key, userID.toString());
		blog.setIsLike(score != null);
	}
}

