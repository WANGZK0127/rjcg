package com.wzk.rjcg.dto;

import lombok.Data;

import java.util.Date;

/**
 * 2024/12/13
 *
 * @author wzk
 * @version 1.0
 */
@Data
public class UserInfoDTO {
	//用户名称
	private String username;
	//城市名称
	private String city;
	//个人介绍，不要超过128个字符
	private String introduce;
	//性别，0：男，1：女
	private Integer gender;
	//生日
	private Date birthday;
}
