package com.wzk.rjcg.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzk.rjcg.mapper.ShopTypeTbDao;
import com.wzk.rjcg.entity.ShopTypeTb;
import com.wzk.rjcg.service.ShopTypeTbService;
import com.wzk.rjcg.util.Result;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商铺类型;(ShopTypeTb)表服务实现类
 *
 * @author makejava
 * @since 2024-12-12 22:35:46
 */
@Service
public class ShopTypeTbServiceImpl extends ServiceImpl<ShopTypeTbDao, ShopTypeTb> implements ShopTypeTbService {
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	@Override
	public Result queryList() {
		String key = "cache:shopType";
		String shopType = stringRedisTemplate.opsForList().leftPop(key);
		//查询缓存
		if(shopType != null){
			//存在直接返回
			List<ShopTypeTb> shopTypeList = JSONUtil.toList(shopType, ShopTypeTb.class);
			return Result.ok(shopTypeList);
		}
		//不存在查询数据库
		List<ShopTypeTb> shopTypeList = query().orderByAsc("sort").list();
		if(shopTypeList == null){
			return Result.fail("商铺分类不存在");
		}
		stringRedisTemplate.opsForList().leftPush(key,JSONUtil.toJsonStr(shopTypeList));
		return Result.ok(shopTypeList);
	}
}

