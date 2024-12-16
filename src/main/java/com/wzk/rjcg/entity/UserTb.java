package com.wzk.rjcg.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户;(UserTb)表实体类
 *
 * @author makejava
 * @since 2024-12-12 15:48:58
 */
@Data
public class UserTb implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//用户ID
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	//手机号
	private String phone;
	//用户名
	private String name;
	//密码
	private String password;
	//头像
	private String icon;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;
	
}

