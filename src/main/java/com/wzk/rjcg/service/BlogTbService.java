package com.wzk.rjcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzk.rjcg.entity.Blog;
import com.wzk.rjcg.util.Result;

/**
 * 博客;(BlogTb)表服务接口
 *
 * @author makejava
 * @since 2024-12-13 14:07:08
 */
public interface BlogTbService extends IService<Blog> {
	/**
	 * 主页博客查询
	 * @param current
	 * @return
	 */
	Result queryHotBlog(Integer current);
	
	/**
	 * 根据id查询博客信息
	 * @param id
	 * @return
	 */
	Result queryBlogById(Long id);
	
	/**
	 * 发布博客
	 * @param blog
	 * @return
	 */
	Result saveBlog(Blog blog);
	
	/**
	 * 点赞
	 * @param id
	 * @return
	 */
	Result likeBlog(Integer id);
	/**
     * 查询博客主页点赞列表
     * @param id
     * @return
     */
	Result queryBlogLikes(Integer id);
	
	/**
	 * 查询关注列表博客
	 * @param max
	 * @param offset
	 * @return
	 */
	Result queryBlogsOfFollow(Long max, Integer offset);
}

