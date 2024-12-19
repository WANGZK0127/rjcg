package com.wzk.rjcg.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzk.rjcg.dto.ScrollResult;
import com.wzk.rjcg.dto.UserDTO;
import com.wzk.rjcg.entity.Blog;
import com.wzk.rjcg.entity.Follow;
import com.wzk.rjcg.entity.UserTb;
import com.wzk.rjcg.mapper.FollowMapper;
import com.wzk.rjcg.service.BlogTbService;
import com.wzk.rjcg.service.IFollowService;
import com.wzk.rjcg.service.UserTbService;
import com.wzk.rjcg.util.Result;
import com.wzk.rjcg.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wzk.rjcg.util.RedisConstants.FEED_KEY;

/**
 * <p>
 * 服务实现类
 * </p>
 */
@Service
@Slf4j
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private UserTbService userService;


	@Override
	public Result follow(Integer followId, Boolean isFollow) {
		//1.获取当前用户id
		Integer userId = UserHolder.getUser().getId();
		if(followId == userId){
			return Result.fail("不能关注自己");
		}
		String key = "follows:" + userId;
		//2.判断是关注还是取关
		if (isFollow) {
			//3.关注，新增数据
			Follow follow = Follow.builder()
					.userId(userId)
					.followUserId(followId)
					.build();
			boolean isSuccess = save(follow);
			if (isSuccess) {
				//将关注的人存入redis的set集合中
				stringRedisTemplate.opsForSet().add(key, followId.toString());
			}
		} else {
			//4.取关，删除数据
			boolean isSuccess = remove(new QueryWrapper<Follow>().eq("user_id", userId).eq("follow_user_id", followId));
			if (isSuccess) {
				//从redis的set集合中移除
				stringRedisTemplate.opsForSet().remove(key, followId.toString());
			}
		}
		return Result.ok();
	}

	@Override
	public Result isFollow(Integer followId) {
		//1.获取当前用户id
		Integer userId = UserHolder.getUser().getId();
		//2.查询当前用户是否关注了该用户
		Integer count = query().eq("user_id", userId).eq("follow_user_id", followId).count();
		return Result.ok(count > 0);
	}

	@Override
	public Result followCommon(Integer id) {
		//1.获取当前用户id
		Integer userId = UserHolder.getUser().getId();
		//2.求交集
		String key = "follows:" + userId;
		String key2 = "follows:" + id;
		Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key, key2);
		//3.解析得到的用户id
		if (intersect == null || intersect.isEmpty()) {
			return Result.ok(Collections.emptyList());
		}
		List<Long> ids = intersect.stream().map(Long::valueOf).collect(Collectors.toList());
		//4.查询用户
		List<UserDTO> userDTOS = userService.listByIds(ids)
				.stream()
				.map(user -> BeanUtil.copyProperties(user, UserDTO.class))
				.collect(Collectors.toList());
		return Result.ok(userDTOS);
	}
	

	@Override
	public Result followMe() {
		Integer userId = UserHolder.getUser().getId();
		List<Integer> followMeIds = query().eq("follow_user_id", userId).select("user_id").list()
				.stream()
				.map(Follow::getUserId)
				.collect(Collectors.toList());
		//根据followIds批量查询用户
		List<UserDTO> userDTOS = userService.listByIds(followMeIds).stream().map(user -> {
			UserDTO userDTO = new UserDTO();
			userDTO.setId(user.getId());
			userDTO.setName(user.getName());
			userDTO.setIcon(user.getIcon());
			return userDTO;
		}).collect(Collectors.toList());
		return Result.ok(userDTOS);
	}
	
	
}
