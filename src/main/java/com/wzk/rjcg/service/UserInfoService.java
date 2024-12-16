package com.wzk.rjcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzk.rjcg.entity.UserInfo;
import com.wzk.rjcg.dto.UserInfoDTO;
import com.wzk.rjcg.util.Result;

/**
 * (UserInfo)表服务接口
 *
 * @author makejava
 * @since 2024-12-13 14:48:02
 */
public interface UserInfoService extends IService<UserInfo> {
	/**
	 * 编辑资料
	 * @param userInfoDTO
	 * @return
	 */
	Result updateInfo(UserInfoDTO userInfoDTO);
	
	Result getInfo(Integer userId);
}

