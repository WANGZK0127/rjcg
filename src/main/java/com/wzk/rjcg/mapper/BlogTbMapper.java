package com.wzk.rjcg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzk.rjcg.entity.Blog;
import org.apache.ibatis.annotations.Param;

/**
 * 博客;(BlogTb)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-13 14:07:08
 */
public interface BlogTbMapper extends BaseMapper<Blog> {
	/**
	 * 评论+1
	 * @param id
	 * @param i
	 */
	void incrReplyCount(@Param("id") Integer id, @Param("count") int count);
}

