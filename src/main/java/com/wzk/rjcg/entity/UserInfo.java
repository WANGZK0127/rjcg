package com.wzk.rjcg.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * (UserInfo)表实体类
 *
 * @author makejava
 * @since 2024-12-13 14:48:02
 */
@Data
public class UserInfo {
	//主键，用户id
	@TableId(value = "user_id")
	private Integer userId;
	//城市名称
	private String city;
	//个人介绍，不要超过128个字符
	private String introduce;
	//获赞数
	@TableField(exist = false)
	private Integer likes;
	//粉丝数量
	private Integer fans;
	//关注的人的数量
	private Integer followee;
	//性别，0：男，1：女
	private Integer gender;
	//生日
	private Date birthday;
	//积分
	private Integer credits;
	//会员级别，0~9级,0代表未开通会员
	private Integer level;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;

	

}

