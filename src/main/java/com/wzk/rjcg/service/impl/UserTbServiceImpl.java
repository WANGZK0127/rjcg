package com.wzk.rjcg.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzk.rjcg.dto.LoginFormDTO;
import com.wzk.rjcg.dto.UserDTO;
import com.wzk.rjcg.util.RedisConstants;
import com.wzk.rjcg.util.Result;
import com.wzk.rjcg.mapper.UserTbDao;
import com.wzk.rjcg.entity.UserTb;
import com.wzk.rjcg.service.UserTbService;
import com.wzk.rjcg.util.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户;(UserTb)表服务实现类
 *
 * @author makejava
 * @since 2024-12-12 15:48:58
 */
@Slf4j
@Service
public class UserTbServiceImpl extends ServiceImpl<UserTbDao, UserTb> implements UserTbService {
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	@Override
	public Result login(LoginFormDTO loginFormDTO) {
		log.info("用户登录信息:{}", JSON.toJSONString(loginFormDTO));
		//验证手机号并根据密码登录
		String phone = loginFormDTO.getPhone();
		UserTb user = query().eq("phone", phone).one();
		if(user == null){
			//用户未找到，请注册
			return Result.fail("用户未找到，请注册");
		}
		String token = UUID.randomUUID().toString(true);
		String key = RedisConstants.LOGIN_USER_KEY + token;
		
		UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
		//将Long类型的id转为String类型,否则报错
		Map<String, Object> map = BeanUtil.beanToMap(userDTO, new HashMap<>(), CopyOptions.create()
				.setIgnoreNullValue(true)
				.setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ? fieldValue.toString() : ""));
		
		//存储到redis中
		stringRedisTemplate.opsForHash().putAll(key,map);
		
		//设置token的过期时间
		stringRedisTemplate.expire(key,RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
		//返回token
		return Result.ok(token);
	}
	
	@Override
	public Result register(LoginFormDTO loginFormDTO) {
		String phone = loginFormDTO.getPhone();
		String password = loginFormDTO.getPassword();
		UserTb user = new UserTb();
		user.setPhone(phone);
		user.setName(SystemConstants.USER_NAME + RandomUtil.randomString(10));
		user.setPassword(password);
		user.setCreateTime(new Date());
		log.info("用户注册信息:{}", JSON.toJSONString(user));
		save(user);
		return Result.ok();
	}
	
	@Override
	public Map<String, UserDTO> batchGetUserInfo(List<String> userNameList) {
		if (CollectionUtils.isEmpty(userNameList)) {
			return Collections.emptyMap();
		}
		//根据用户名列表查询用户信息
		List<UserTb> userList = query().in("id", userNameList).list();
		log.info("用户信息:{}", JSON.toJSONString(userList));
		Map<String, UserDTO> result = new HashMap<>();
		for (UserTb user : userList) {
			UserDTO userDTO = new UserDTO();
			userDTO.setId(user.getId());
			userDTO.setName(user.getName());
			userDTO.setIcon(user.getIcon());
			result.put(user.getId().toString(), userDTO);
		}
		return result;
	}
	
	
}

