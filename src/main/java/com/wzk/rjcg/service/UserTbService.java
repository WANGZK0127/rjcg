package com.wzk.rjcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzk.rjcg.dto.LoginFormDTO;
import com.wzk.rjcg.dto.UserDTO;
import com.wzk.rjcg.util.Result;
import com.wzk.rjcg.entity.UserTb;

import java.util.List;
import java.util.Map;

/**
 * 用户;(UserTb)表服务接口
 *
 * @author makejava
 * @since 2024-12-12 15:48:58
 */
public interface UserTbService extends IService<UserTb> {
	
	Result login(LoginFormDTO loginFormDTO);
	
	Result register(LoginFormDTO loginFormDTO);
	
	/**
	 * 批量获取用户信息
	 * @param userNameList
	 * @return
	 */
	Map<String, UserDTO> batchGetUserInfo(List<String> userNameList);
	
	/**
	 * 查看头像，用户名信息
	 * @return
	 */
	Result me();
}

