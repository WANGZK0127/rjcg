package com.wzk.rjcg.Controller;

import com.google.common.base.Preconditions;
import com.wzk.rjcg.dto.LoginFormDTO;
import com.wzk.rjcg.dto.UserDTO;
import com.wzk.rjcg.entity.UserInfo;
import com.wzk.rjcg.service.UserInfoService;
import com.wzk.rjcg.util.Result;
import com.wzk.rjcg.service.UserTbService;
import com.wzk.rjcg.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;

import javax.annotation.Resource;

/**
 * 用户
 * 2024/12/11
 * 
 * @author wzk
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
	@Resource
	public UserTbService userTbService;
	
	@Resource
	public UserInfoService userInfoService;
	
	/**
	 * 登录
	 * @param loginFormDTO
	 * @return
	 */
	@RequestMapping("/login")
	public Result login(@RequestBody LoginFormDTO loginFormDTO){
		try {
			if (log.isInfoEnabled()) {
				log.info("UserController.login.loginFormDTO:{}", JSON.toJSONString(loginFormDTO));
			}
			Preconditions.checkArgument(!StringUtils.isBlank(loginFormDTO.getPhone()), "手机号不能为空");
			return userTbService.login(loginFormDTO);	
		}catch (Exception e){
			log.error("UserController.login().error:{}",e.getMessage(),e);
			return Result.fail("登录失败");
		}
		
	}
	
	/**
	 * 注册
	 * @param loginFormDTO
	 * @return
	 */
	@RequestMapping("/register")
	public Result register(@RequestBody LoginFormDTO loginFormDTO){
		try {
			if (log.isInfoEnabled()) {
				log.info("UserController.register.loginFormDTO:{}", JSON.toJSONString(loginFormDTO));
			}
			Preconditions.checkArgument(!StringUtils.isBlank(loginFormDTO.getPhone()), "手机号不能为空");
			return userTbService.register(loginFormDTO);
		}catch (Exception e) {
			log.error("UserController.register.error:{}", e.getMessage(),e);
			return Result.fail("注册失败");
		}
	}
	
	/**
	 * 主页信息
	 * @return
	 */
	@RequestMapping("/me")
	public Result me(){
		UserDTO user = UserHolder.getUser();
		return Result.ok(user);
	}
	
}
