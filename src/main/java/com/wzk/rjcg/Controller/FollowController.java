package com.wzk.rjcg.Controller;


import com.wzk.rjcg.service.IFollowService;
import com.wzk.rjcg.util.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 关注
 *
 */
@RestController
@RequestMapping("/follow")
public class FollowController {

	@Resource
	private IFollowService followService;

	@PutMapping("/{id}/{isFollow}")
	public Result follow(@PathVariable Integer id, @PathVariable Boolean isFollow){
		return followService.follow(id,isFollow);
	}
	
	@RequestMapping("/if/{id}")
	public Result isFollow(@PathVariable Integer id) {
		return followService.isFollow(id);
	}
	
	@GetMapping("/common/{id}")
	public Result commonFollow(@PathVariable Integer id){
		return followService.followCommon(id);
	}
	
	/**
	 * 我的粉丝
	 * @return
	 */
	@RequestMapping("/followMe")
	public Result followMe(){
		return followService.followMe();
	}
}
