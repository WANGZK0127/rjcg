package com.wzk.rjcg.Controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Preconditions;
import com.wzk.rjcg.dto.UserDTO;
import com.wzk.rjcg.entity.Blog;
import com.wzk.rjcg.service.BlogTbService;
import com.wzk.rjcg.util.Result;
import com.wzk.rjcg.util.SystemConstants;
import com.wzk.rjcg.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 博客
 *
 * @author makejava
 * @since 2024-12-13 14:07:08
 */
@Slf4j
@RestController
@RequestMapping("/blog")
public class BlogTbController{

	@Resource
	private BlogTbService blogTbService;
	
	/**
	 * 
	 * @param blog
	 * @return
	 */
	@PostMapping
	public Result saveBlog(@RequestBody Blog blog) {
		try {
			if (log.isInfoEnabled()) {
				log.info("BlogTBController.save.blog:{}", JSON.toJSONString(blog));
			}
			Preconditions.checkArgument(!StringUtils.isBlank(blog.getTitle()), "标题不能为空");
			Preconditions.checkArgument(!StringUtils.isBlank(blog.getContent()), "内容不能为空");
			Preconditions.checkArgument(!StringUtils.isBlank(blog.getImages()), "内容不能为空");
			return blogTbService.saveBlog(blog);
		} catch (Exception e) {
			log.error("BlogTBController.saveBlog().error:{}", e.getMessage(), e);
			return Result.fail("发布博客失败");
		}
	}
	/**
	 * 主页博客查询
	 * @param current
	 * @return
	 */
	@RequestMapping("/hot")
	public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
		return blogTbService.queryHotBlog(current);
	}
	
	/**
	 * 个人博客查询
	 * @param current
	 * @return
	 */
	@RequestMapping("/of/me")
	public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
		// 获取登录用户
		UserDTO user = UserHolder.getUser();
		// 根据用户查询
		Page<Blog> page = blogTbService.query()
				.eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
		// 获取当前页数据
		List<Blog> records = page.getRecords();
		return Result.ok(records);
	}
	
	/**
	 * 博客详情
	 * @param id
	 * @return
	 */
	@RequestMapping("/{id}")
	public Result queryBlogById(@PathVariable("id") Long id) {
		return blogTbService.queryBlogById(id);
	}
	
	/**
	 * 博客点赞
	 * @param id  博客id
	 */
	@RequestMapping("/like/{id}")
	public Result likeBlog(@PathVariable("id") Integer id) {
		return blogTbService.likeBlog(id);
	}
	/**
	 * 博客主页点赞列表
	 * @param id 博客id
     * @return
	 */
	@RequestMapping("/likes/{id}")
    public Result queryBlogLikes(@PathVariable("id") Integer id) {
		return blogTbService.queryBlogLikes(id);
	}
	
	/**
	 * 查询关注列表博客
	 * @param max
	 * @param offset
	 * @return
	 */
	@RequestMapping("/myFollow")
	public Result myFollow(@RequestParam("lastId") Long max,
						   @RequestParam(value = "offset",defaultValue = "0") Integer offset){
		return blogTbService.queryBlogsOfFollow(max,offset);
	}
}

