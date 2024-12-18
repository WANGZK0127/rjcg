package com.wzk.rjcg.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzk.rjcg.dto.SaveCommentReplyReq;
import com.wzk.rjcg.entity.Blog;
import com.wzk.rjcg.entity.CommentReply;
import com.wzk.rjcg.mapper.BlogTbMapper;
import com.wzk.rjcg.mapper.CommentReplyMapper;
import com.wzk.rjcg.service.CommentReplyService;
import com.wzk.rjcg.util.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 评论及回复信息 服务实现类
 */
@Service
public class CommentReplyServiceImpl extends ServiceImpl<CommentReplyMapper, CommentReply> implements CommentReplyService {

    @Resource
    private BlogTbMapper blogTbMapper;


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
        blogTbMapper.incrReplyCount(blog.getId(), 1);
        return save(comment);
    }
    

//    @Override
//    public List<CommentReplyVO> listComment(Integer id) {
//        LambdaQueryWrapper<CommentReply> query = Wrappers.<CommentReply>lambdaQuery()
//                .eq(CommentReply::getMomentId, req.getId())
//                .eq(CommentReply::getIsDeleted, IsDeletedFlagEnum.UN_DELETED.getCode())
//                .select(CommentReply::getId,
//                        CommentReply::getMomentId,
//                        CommentReply::getReplyType,
//                        CommentReply::getContent,
//                        CommentReply::getPicUrls,
//                        CommentReply::getCreatedBy,
//                        CommentReply::getToUser,
//                        CommentReply::getCreatedTime,
//                        CommentReply::getParentId);
//        List<CommentReply> list = list(query);
//        List<String> userNameList = list.stream().map(CommentReply::getCreatedBy).distinct().collect(Collectors.toList());
//        Map<String, UserInfo> userInfoMap = userRpc.batchGetUserInfo(userNameList);
//        UserInfo defaultUser = new UserInfo();
//        List<CommentReplyVO> voList = list.stream().map(item -> {
//            CommentReplyVO vo = new CommentReplyVO();
//            vo.setId(item.getId());
//            vo.setMomentId(item.getMomentId());
//            vo.setReplyType(item.getReplyType());
//            vo.setContent(item.getContent());
//            if (Objects.nonNull(item.getPicUrls())) {
//                vo.setPicUrlList(JSONArray.parseArray(item.getPicUrls(), String.class));
//            }
//            if (item.getReplyType() == 2) {
//                vo.setFromId(item.getCreatedBy());
//                vo.setToId(item.getToUser());
//            }
//            vo.setParentId(item.getParentId());
//            UserInfo user = userInfoMap.getOrDefault(item.getCreatedBy(), defaultUser);
//            vo.setUserName(user.getNickName());
//            vo.setAvatar(user.getAvatar());
//            vo.setCreatedTime(item.getCreatedTime().getTime());
//            return vo;
//        }).collect(Collectors.toList());
//        return TreeUtils.buildTree(voList);
//    }

}
