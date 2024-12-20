package com.wzk.rjcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzk.rjcg.entity.Shop;
import com.wzk.rjcg.util.Result;

/**
 *  服务类
 */
public interface IShopService extends IService<Shop> {

	/**
	 * 根据ID查询店铺信息
	 * @param id
	 * @return
	 */
	Result queryById(Integer id);

	/**
	 * 更新商铺
	 * @param shop
	 * @return
	 */
	Result updateShop(Shop shop);

	/**
	 * 根据类型分页查询
	 * @param typeId
	 * @param current
	 * @return
	 */
	Result queryShopByType(Integer typeId, Integer current);
	
	/**
	 * 根据博客id查询博客内商铺信息
	 * @param id
	 * @return
	 */
	Result inBlog(Integer id);
	
	/**
	 * 根据ID查询店铺详情
	 * @param id
	 * @return
	 */
	Result detail(Integer id);
}
