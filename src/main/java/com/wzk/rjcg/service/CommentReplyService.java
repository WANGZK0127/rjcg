package com.wzk.rjcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzk.rjcg.dto.CommentReplyVO;
import com.wzk.rjcg.dto.SaveCommentReplyReq;
import com.wzk.rjcg.entity.CommentReply;

import java.util.List;

/**
 * <p>
 * 评论及回复信息 服务类
 * </p>
 *
 * @author ChickenWing
 * @since 2024/05/16
 */
public interface CommentReplyService extends IService<CommentReply> {
    /**
     * 发布评论回复
     * @param req
     * @return
     */
    Boolean saveComment(SaveCommentReplyReq req);
    
    /**
     * 获取评论回复列表
     * @param id
     * @return
     */
     List<CommentReplyVO> listComment(Integer id);

}
