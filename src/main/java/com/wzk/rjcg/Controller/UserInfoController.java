package com.wzk.rjcg.Controller;

import com.wzk.rjcg.dto.UserInfoDTO;
import com.wzk.rjcg.service.UserInfoService;
import com.wzk.rjcg.util.Result;
import com.wzk.rjcg.util.UserHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户信息
 * 2024/12/13
 *
 * @author wzk
 * @version 1.0
 */
@RestController
@RequestMapping("/userInfo")
public class UserInfoController {
	@Resource
	public UserInfoService userInfoService;
	
	@RequestMapping("/update")
	public Result update(@RequestBody UserInfoDTO userInfoDTO){
		return userInfoService.updateInfo(userInfoDTO);
	}
	
	/**
	 * 用户信息
	 * @return
	 */
	@RequestMapping("/info")
	public Result info(){
		Integer userId = UserHolder.getUser().getId();
		return userInfoService.getInfo(userId);
	}
}
