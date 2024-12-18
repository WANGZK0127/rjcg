package com.wzk.rjcg.Controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Preconditions;
import com.wzk.rjcg.dto.CommentReplyVO;
import com.wzk.rjcg.dto.SaveCommentReplyReq;
import com.wzk.rjcg.entity.Blog;
import com.wzk.rjcg.service.BlogTbService;
import com.wzk.rjcg.service.CommentReplyService;
import com.wzk.rjcg.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 评论回复
 *
 * @author wzk
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/commentReply")
public class CommentReplyController {
	@Resource
	private CommentReplyService commentReplyService;
	@Resource
	private BlogTbService blogTbService;
	/**
	 * 发布内容
	 * @param req
	 * @return
	 */
	@PostMapping("/save")
	public Result<Boolean> save(@RequestBody SaveCommentReplyReq req){
		try {
			if (log.isInfoEnabled()) {
				log.info("发布内容入参{}", JSON.toJSONString(req));
			}
			Preconditions.checkArgument(Objects.nonNull(req), "参数不能为空！");
			Preconditions.checkArgument(Objects.nonNull(req.getReplyType()), "类型不能为空！");
			Preconditions.checkArgument(Objects.nonNull(req.getBlogId()), "博客ID不能为空！");
			Blog moment = blogTbService.getById(req.getBlogId());
			Preconditions.checkArgument((Objects.nonNull(req.getContent())), "内容不能为空！");
			Boolean result = commentReplyService.saveComment(req);
			if (log.isInfoEnabled()) {
				log.info("发布内容{}", JSON.toJSONString(result));
			}
			return Result.ok(result);
		} catch (IllegalArgumentException e) {
			log.error("参数异常！错误原因{}", e.getMessage(), e);
			return Result.fail(e.getMessage());
		} catch (Exception e) {
			log.error("发布内容异常！错误原因{}", e.getMessage(), e);
			return Result.fail("发布内容异常！");
		}
	}
	
	/**
	 * 查询该博客下的评论
	 */
	@PostMapping(value = "/list")
	public Result<List<CommentReplyVO>> list(@RequestParam("id") Integer id) {
		try {
			if (log.isInfoEnabled()) {
				log.info("获取评论内容入参{}", JSON.toJSONString(id));
			}
			Preconditions.checkArgument(Objects.nonNull(id), "内容ID不能为空！");
			List<CommentReplyVO> result = commentReplyService.listComment(id);
			if (log.isInfoEnabled()) {
				log.info("获取评论内容{}", JSON.toJSONString(result));
			}
			return Result.ok(result);
		} catch (IllegalArgumentException e) {
			log.error("参数异常！错误原因{}", e.getMessage(), e);
			return Result.fail(e.getMessage());
		} catch (Exception e) {
			log.error("获取评论内容异常！错误原因{}", e.getMessage(), e);
			return Result.fail("获取评论内容异常！");
		}
	}
}
