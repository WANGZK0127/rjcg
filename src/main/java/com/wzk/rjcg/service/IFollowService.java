package com.wzk.rjcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzk.rjcg.entity.Follow;
import com.wzk.rjcg.util.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IFollowService extends IService<Follow> {

	/**
	 * 关注
	 * @param id 用户id
	 * @param isFollow 是否关注
	 * @return
	 */
	Result follow(Integer id, Boolean isFollow);

	/**
	 * 判断是否关注
	 * @param id
	 * @return
	 */
	Result isFollow(Integer id);

	/**
	 * 共同关注
	 * @param id
	 * @return
	 */
	Result followCommon(Integer id);
	
	/**
	 * 我的粉丝
	 * @return
	 */
	Result followMe();
	
	
}
