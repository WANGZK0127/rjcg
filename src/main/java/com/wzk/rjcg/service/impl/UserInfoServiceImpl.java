package com.wzk.rjcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzk.rjcg.dto.UserDTO;
import com.wzk.rjcg.entity.Blog;
import com.wzk.rjcg.dto.UserInfoDTO;
import com.wzk.rjcg.entity.UserTb;
import com.wzk.rjcg.mapper.UserInfoDao;
import com.wzk.rjcg.entity.UserInfo;
import com.wzk.rjcg.service.BlogTbService;
import com.wzk.rjcg.service.IFollowService;
import com.wzk.rjcg.service.UserInfoService;
import com.wzk.rjcg.service.UserTbService;
import com.wzk.rjcg.util.RedisConstants;
import com.wzk.rjcg.util.Result;
import com.wzk.rjcg.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * (UserInfo)表服务实现类
 *
 * @author makejava
 * @since 2024-12-13 14:48:02
 */
@Service
@Slf4j
public class UserInfoServiceImpl extends ServiceImpl<UserInfoDao, UserInfo> implements UserInfoService {
	@Resource
	public UserTbService userTbService;
	@Resource
	public BlogTbService blogTbService;
	@Resource
	private IFollowService followService;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Override
	@Transactional
	public Result updateInfo(UserInfoDTO userInfoDTO) {
		UserDTO userDTO = UserHolder.getUser();
		String name = userInfoDTO.getUsername();
		//根据userDTO的id修改user的name和password
		userTbService.update(new UpdateWrapper<UserTb>()
				.eq("id", userDTO.getId()).set("name", name));
		UserInfo userInfo = new UserInfo();
		userInfo.setCity(userInfoDTO.getCity());
		userInfo.setBirthday(userInfoDTO.getBirthday());
		userInfo.setGender(userInfoDTO.getGender());
		userInfo.setIntroduce(userInfoDTO.getIntroduce());
		userInfo.setUpdateTime(new Date());
		//更新UserInfo表
		//去redis查询该用户的关注数量
		String followCount = stringRedisTemplate.opsForValue()
				.get(RedisConstants.FOLLOW_COUNT_KEY + userDTO.getId());
		//查询粉丝数量
		Integer fanCount = followService.query().eq("follow_user_id", userDTO.getId()).count();
		
		userInfo.setFans(fanCount);
		userInfo.setFollowee(followCount == null ? 0 : Integer.parseInt(followCount));
		update(userInfo,new UpdateWrapper<UserInfo>().eq("user_id",userDTO.getId()));
		return Result.ok();
	}
	
	@Override
	public Result getInfo(Integer userId) {
		// 查询详情
		UserInfo info = getById(userId);
		if (info == null) {
			// 没有详情，应该是第一次查看详情
			info = new UserInfo();
			info.setUserId(userId);
			info.setLikes(0);
			info.setCreateTime(new Date());
			info.setIntroduce("这个人很懒，什么都没有写");
			info.setCity("未知");
			info.setGender(3);
			info.setFans(0);
			info.setFollowee(0);
			save(info);
			
		}else{
			//查询得到总赞
			//1.查询用户发布博客的赞数，相加
			List<Blog> blogs = blogTbService.list(new QueryWrapper<Blog>().eq("user_id", userId));
			log.info("blogs:{}", blogs);
			//2.查询用户收到的赞数，相加
			int totalLike = blogs.stream().mapToInt(Blog::getLiked).sum();
			info.setLikes(totalLike);
			
		}
		// 返回
		return Result.ok(info);
	}
}

