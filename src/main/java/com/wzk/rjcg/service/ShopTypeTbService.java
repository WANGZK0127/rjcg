package com.wzk.rjcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzk.rjcg.entity.ShopTypeTb;
import com.wzk.rjcg.util.Result;

/**
 * 商铺类型;(ShopTypeTb)表服务接口
 *
 * @author makejava
 * @since 2024-12-12 22:35:46
 */
public interface ShopTypeTbService extends IService<ShopTypeTb> {
	
	Result queryList();
}

