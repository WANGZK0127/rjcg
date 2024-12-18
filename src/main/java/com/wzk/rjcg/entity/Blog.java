package com.wzk.rjcg.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 博客;(BlogTb)表实体类
 *
 * @author makejava
 * @since 2024-12-13 14:07:08
 */
@Data
public class Blog {
	//主键
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	//商户id
	private Integer shopId;
	//用户id
	private Integer userId;
	/**
	 * 用户图标
	 */
	@TableField(exist = false)
	private String icon;
	/**
	 * 用户姓名
	 */
	@TableField(exist = false)
	private String name;
	/**
	 * 是否点赞过了
	 */
	@TableField(exist = false)
	private Boolean isLike;
	//标题
	private String title;
	//探店的图片，最多9张，多张以',' 隔开
	private String images;
	//探店的文字描述
	private String content;
	//点赞数量
	private Integer liked;
	//评论数量
	private Integer comments;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;

}

