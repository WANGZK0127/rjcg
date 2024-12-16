package com.wzk.rjcg.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 商铺类型;(ShopTypeTb)表实体类
 *
 * @author makejava
 * @since 2024-12-12 22:35:46
 */
@Data
public class ShopTypeTb implements Serializable {
	//主键
	@TableId(value = "id", type = IdType.AUTO)
	
	private Integer id;
	//类型名称
	private String name;
	//图标
	private String icon;
	//顺序
	private Integer sort;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;

	
}

