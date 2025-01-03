package com.wzk.rjcg.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzk.rjcg.dto.CommentReplyVO;
import com.wzk.rjcg.dto.SaveCommentReplyReq;
import com.wzk.rjcg.dto.UserDTO;
import com.wzk.rjcg.entity.Blog;
import com.wzk.rjcg.entity.CommentReply;
import com.wzk.rjcg.entity.UserInfo;
import com.wzk.rjcg.entity.UserTb;
import com.wzk.rjcg.mapper.BlogTbMapper;
import com.wzk.rjcg.mapper.CommentReplyMapper;
import com.wzk.rjcg.service.CommentReplyService;
import com.wzk.rjcg.service.UserTbService;
import com.wzk.rjcg.util.TreeUtils;
import com.wzk.rjcg.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 评论及回复信息 服务实现类
 */
@Service
@Slf4j
public class CommentReplyServiceImpl extends ServiceImpl<CommentReplyMapper, CommentReply> implements CommentReplyService {

    @Resource
    private BlogTbMapper blogTbMapper;
    @Resource
    private UserTbService userTbService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveComment(SaveCommentReplyReq req) {
        //查询要评论的博客
        Blog blog = blogTbMapper.selectById(req.getBlogId());
        CommentReply comment = new CommentReply();
        comment.setBlogId(req.getBlogId());
        comment.setReplyType(req.getReplyType());
        Integer loginId = UserHolder.getUser().getId();
        // 1评论 2回复
        if (req.getReplyType() == 1) {
            comment.setParentId(-1L);
            comment.setToId(req.getTargetId());
            comment.setToUser(loginId.toString());
            comment.setToUserAuthor(Objects.nonNull(blog.getUserId()) && loginId.equals(blog.getUserId()) ? 1 : 0);
        } else {
            comment.setParentId(req.getTargetId());
            comment.setReplyId(req.getTargetId());
            comment.setReplyUser(loginId.toString());
            comment.setReplayAuthor(Objects.nonNull(blog.getUserId()) && loginId.equals(blog.getUserId()) ? 1 : 0);
        }
        comment.setContent(req.getContent());
        
        comment.setCreatedBy(loginId.toString());
        comment.setCreatedTime(new Date());
        log.info("保存评论内容{}", JSON.toJSONString(comment));
        blogTbMapper.incrReplyCount(blog.getId(), 1);
        return save(comment);
    }
    

    @Override
    public List<CommentReplyVO> listComment(Integer id) {
        Integer userId = blogTbMapper.selectById(id).getUserId();
        LambdaQueryWrapper<CommentReply> query = Wrappers.<CommentReply>lambdaQuery()
                .eq(CommentReply::getBlogId, id)
                .select(CommentReply::getId,
                        CommentReply::getBlogId,
                        CommentReply::getReplyType,
                        CommentReply::getContent,
                        CommentReply::getCreatedBy,
                        CommentReply::getToUser,
                        CommentReply::getReplyUser,
                        CommentReply::getCreatedTime,
                        CommentReply::getParentId);
        List<CommentReply> list = list(query);
        List<String> userNameList = list.stream().map(CommentReply::getCreatedBy).distinct().collect(Collectors.toList());
        Map<String, UserDTO> userInfoMap = userTbService.batchGetUserInfo(userNameList);
        UserDTO defaultUser = new UserDTO();
        List<CommentReplyVO> voList = list.stream().map(item -> {
            CommentReplyVO vo = new CommentReplyVO();
            vo.setId(item.getId());
            vo.setBlogId(item.getBlogId());
            vo.setReplyType(item.getReplyType());
            vo.setContent(item.getContent());
            if (item.getReplyType() == 1) {
                vo.setFromId(item.getCreatedBy());
                vo.setToId(item.getToUser());
            } else if (item.getReplyType() == 2) {
                vo.setFromId(item.getCreatedBy());
                vo.setToId(item.getReplyUser());
            }
            vo.setIsAuthor(false);
            if(Objects.equals(vo.getFromId(), userId.toString())){
                vo.setIsAuthor(true);
            }
            vo.setParentId(item.getParentId());
            UserDTO user = userInfoMap.getOrDefault(item.getCreatedBy(), defaultUser);
            vo.setUserName(user.getName());
            vo.setAvatar(user.getIcon());
            vo.setCreatedTime(item.getCreatedTime().getTime());
            return vo;
        }).collect(Collectors.toList());
        return TreeUtils.buildTree(voList);
    }

}
