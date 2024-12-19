package com.wzk.rjcg.dto;

import com.wzk.rjcg.common.TreeNode;
import lombok.Getter;
import lombok.Setter;
import lombok.*;
import java.io.Serializable;

/**
 * <p>
 * 评论及回复信息
 * </p>
 *
 * @author ChickenWing
 * @since 2024/05/16
 */
@Getter
@Setter
public class CommentReplyVO extends TreeNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 原始博客ID
     */
    private Long blogId;

    /**
     * 回复类型 1评论 2回复
     */
    private Integer replyType;

    /**
     * 内容
     */
    private String content;


    private String fromId;

    private String toId;
    
    //该注解默认值为false
    
    private Boolean isAuthor; 

    private Long parentId;

    private String userName;

    private String avatar;

    private long createdTime;

    @Override
    public Long getNodeId() {
        return id;
    }

    @Override
    public Long getNodePId() {
        return parentId;
    }

}
