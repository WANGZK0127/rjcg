package com.wzk.rjcg.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzk.rjcg.dto.ShopDTO;
import com.wzk.rjcg.entity.Shop;
import com.wzk.rjcg.mapper.ShopMapper;
import com.wzk.rjcg.service.BlogTbService;
import com.wzk.rjcg.service.IShopService;
import com.wzk.rjcg.util.RedisConstants;
import com.wzk.rjcg.util.Result;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wzk.rjcg.util.SystemConstants;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private BlogTbService blogTbService;
	
	@Override
	public Result queryById(Integer id) {
		//缓存穿透解决
		//Shop shop = cacheClient.queryWithPassThrough(RedisConstants.CACHE_SHOP_KEY, id, Shop.class, this::getById, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
		//缓存击穿解决
		//Shop shop = cacheClient.queryWithLogicalExpire(RedisConstants.CACHE_SHOP_KEY, id, Shop.class, this::getById, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
		Shop shop = query().eq("id", id).one();
		if (shop == null) {
			return Result.fail("商铺不存在");
		}
		return Result.ok(shop);
	}
	
	@Override
	@Transactional
	public Result updateShop(Shop shop) {
		//1.更新数据库
		Integer id = shop.getId();
		if (id == null) {
			return Result.fail("商铺id不能为空");
		}
		updateById(shop);
		//2.删除缓存
		//stringRedisTemplate.delete(RedisConstants.CACHE_SHOP_KEY + id);
		return Result.ok();
	}
	
	@Override
	public Result queryShopByType(Integer typeId, Integer current) {
			// 根据类型分页查询
			Page<Shop> page = query()
					.eq("type_id", typeId)
					.page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
			// 返回数据
			return Result.ok(page.getRecords());
	}
	
	@Override
	public Result inBlog(Integer id) {
		Integer shopId = blogTbService.query().eq("id", id).one().getShopId();
		if(shopId == 0){
			return Result.ok();
		}
		ShopDTO shopDTO = new ShopDTO();
		BeanUtil.copyProperties(query().eq("id", shopId).one(), shopDTO);
		return Result.ok(shopDTO);
	}
	
	@Override
	public Result detail(Integer id) {
		return Result.ok(query().eq("id", id).one());
	}
}
