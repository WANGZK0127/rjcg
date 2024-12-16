package com.wzk.rjcg.Controller;

import com.wzk.rjcg.service.ShopTypeTbService;
import com.wzk.rjcg.util.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 商铺类型
 *
 * @author makejava
 * @since 2024-12-12 22:35:45
 */
@RestController
@RequestMapping("/shopType")
public class ShopTypeTbController {
	/**
	 * 服务对象
	 */
	@Resource
	private ShopTypeTbService shopTypeTbService;
	
	@RequestMapping("list")
	public Result queryTypeList() {
		return shopTypeTbService.queryList();
	}
}

