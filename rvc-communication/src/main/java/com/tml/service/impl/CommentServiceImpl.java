package com.tml.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.client.UserServiceClient;
import com.tml.designpattern.chain.ext.CommentExistApproveChain;
import com.tml.designpattern.chain.ext.LastStepApproveChain;
import com.tml.designpattern.chain.ext.PostExistApproveChain;
import com.tml.designpattern.chain.ext.UserExistApproveChain;
import com.tml.domain.dto.CoinDto;
import com.tml.domain.dto.CommentDto;
import com.tml.domain.dto.PageInfo;
import com.tml.handler.exception.SystemException;
import com.tml.mapper.CommentMapper;

import com.tml.mapper.LikeCommentMapper;
import com.tml.pojo.VO.UserInfoVO;
import com.tml.domain.entity.Comment;
import com.tml.domain.entity.LikeComment;
import com.tml.domain.vo.CommentVo;
import com.tml.service.CommentService;
import com.tml.utils.BeanCopyUtils;
import com.tml.utils.Uuid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tml.constant.DetectionConstants.*;
import static com.tml.constant.enums.AppHttpCodeEnum.*;


/**
 * @NAME: CommentServiceImpl
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/30
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final CommentMapper commentMapper;
    private final LikeCommentMapper likeCommentMapper;
    private final UserServiceClient userServiceClient;


//责任链
    private final UserExistApproveChain userExistApproveChain;
    private final PostExistApproveChain postExistApproveChain;
    private final LastStepApproveChain lastStepApproveChain;
    private final CommentExistApproveChain commentExistApproveChain;


    @Override
    public String comment(CommentDto commentDto, String uid) {
//数据校验  用户存在 -》 帖子存在
        userExistApproveChain.setNext(uid,postExistApproveChain);
        postExistApproveChain.setNext(commentDto.getPostId(),lastStepApproveChain);
        userExistApproveChain.approve();
        commentCheck(commentDto);

        String commentId = Uuid.getUuid();
        Comment comment = Convert.convert(new TypeReference<Comment>(){}, commentDto);
        comment.setPostCommentId(commentId);
        comment.setUserId(uid);
        save(comment);
        return commentId;
    }

    @Override
    public List<CommentVo> list(PageInfo<String> params, String uid) {
        String postId = params.getData();
        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();
//数据校验   帖子存在
        postExistApproveChain.setNext(postId,lastStepApproveChain);
        postExistApproveChain.approve();
//分页获取数据
        Page<Comment> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, postId)
//                审核成功
                .eq(Comment::getDetectionStatus, DETECTION_SUCCESS)
//                根评论
                .eq(Comment::getRootCommentId,"-1");
        List<Comment> rootComment  = this.page(page,queryWrapper).getRecords();

        //获取所有的用户id root评论只有创建的用户id
        List<String> userIds = rootComment.stream()
                .map(record -> record.getUserId())
                .collect(Collectors.toList());

        Map<String, UserInfoVO> data = getUsersInfo(userIds);

        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(rootComment, CommentVo.class);

//将评论和用户信息进行封装
        for (int i = 0; i < commentVos.size(); i++) {
            CommentVo commentVo = commentVos.get(i);
            commentVo.setLike(false);
            //如果用户登录 封装数据
            if (!Strings.isBlank(uid)){
                LambdaUpdateWrapper<LikeComment> likeCommentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                likeCommentLambdaUpdateWrapper.eq(LikeComment::getUid, uid)
                        .eq(LikeComment::getCommentId, commentVo.getPostCommentId());
                //封装用户是否点赞评论
                LikeComment likeComment = likeCommentMapper.selectOne(likeCommentLambdaUpdateWrapper);
                if (!Objects.isNull(likeComment)){
                    commentVo.setLike(true);
                }
            }
            commentVo.setUser(data.get(rootComment.get(i).getUserId()));
        }

        return commentVos;
    }

    @Override
    public List<CommentVo> childrenList(PageInfo<String> params,String uid) {
        String rootCommentId = params.getData();
        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();

//分页
        Page<Comment> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootCommentId, rootCommentId)
                .eq(Comment::getDetectionStatus, DETECTION_SUCCESS)
                .eq(Comment::getRootCommentId,rootCommentId);
        List<Comment> records = this.page(page,queryWrapper).getRecords();


//获取所有的用户id（回复的和被回复的）
        List<String> sonUserIds = records.stream()
                .flatMap(son -> Stream.of(son.getUserId(), son.getToUserId()))
                .collect(Collectors.toList());
//        调用用户服务获取数据
        Map<String, UserInfoVO> data = getUsersInfo(sonUserIds);

        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(records, CommentVo.class);
        for (int i = 0; i < commentVos.size(); i++) {
            CommentVo commentVo = commentVos.get(i);
            commentVo.setLike(false);
            if (!Strings.isBlank(uid)){
                LambdaUpdateWrapper<LikeComment> likeCommentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                likeCommentLambdaUpdateWrapper.eq(LikeComment::getUid,uid)
                        .eq(LikeComment::getCommentId,commentVo.getPostCommentId());
                LikeComment likeComment = likeCommentMapper.selectOne(likeCommentLambdaUpdateWrapper);
                if (!Objects.isNull(likeComment)){
                    commentVo.setLike(true);
                }
            }
            commentVo.setUser(data.get(records.get(i).getUserId()));
            commentVo.setReplayUser(data.get(records.get(i).getToUserId()));
        }
        return commentVos;
    }

    @Override
    @Transactional
    public void favorite(CoinDto coinDto, String uid) {
        //数据校验  用户存在 -》 评论存在
        userExistApproveChain.setNext(uid,commentExistApproveChain);
        commentExistApproveChain.setNext(coinDto.getId(),lastStepApproveChain);
        userExistApproveChain.approve();


        //1、点赞    添加关系表中的记录       post表 like_num +1
        //0、取消点赞    删除关系表中的记录       post表 like_num -1
        if (coinDto.judgeType()){
//            String uuid = ;
            LikeComment likeComment = new LikeComment(Uuid.getUuid(),  coinDto.getId(),uid);
            try {
                likeCommentMapper.insert(likeComment);
            } catch (Exception e) {
                throw new SystemException(FAVORITE_ERROR);
            }
//            LambdaUpdateWrapper<Comment> updateWrapper = Wrappers.<Comment>lambdaUpdate()
//                    .eq(Comment::getPostCommentId, coinDto.getId());
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("post_comment_id", coinDto.getId());
            commentMapper.addFavorite(commentQueryWrapper);
//                    .setSql("comment_like_count = comment_like_count + 1");
//            commentMapper.update(null,updateWrapper);
        } else {
//            LambdaQueryWrapper<LikeComment> likePostLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            likePostLambdaQueryWrapper.eq(LikeComment::getUid,uid)
//                    .eq(LikeComment::getCommentId,coinDto.getId());
//
//            if (likeCommentMapper.delete(likePostLambdaQueryWrapper) == 0){
//                throw new SystemException(FAVORITE_ERROR);
//            }
            QueryWrapper<LikeComment> likecommentQueryWrapper = new QueryWrapper<>();
            likecommentQueryWrapper.eq("uid", uid)
                    .eq("comment_id",coinDto.getId());
            if (commentMapper.deleteLikeComment(likecommentQueryWrapper) == 0){
                throw new SystemException(FAVORITE_ERROR);
            }


            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("post_comment_id", coinDto.getId());
            commentMapper.disFavorite(commentQueryWrapper);
//            LambdaUpdateWrapper<Comment> updateWrapper = Wrappers.<Comment>lambdaUpdate()
//                    .eq(Comment::getPostCommentId, coinDto.getId())
//                    .setSql("comment_like_count = comment_like_count - 1");
//            commentMapper.update(null,updateWrapper);


            return;
        }
    }


    public Map<String, UserInfoVO> getUsersInfo(List<String> userIds){
        //调用用户服务获取评论创建人的信息
        Map<String, UserInfoVO> data = null;
        try {
            data = userServiceClient.list(userIds).getData();
        } catch (Exception e) {
            throw new RuntimeException("用户服务调用异常");
        }
        return data;
    }

    public void commentCheck(CommentDto commentDto){
        /**
         * 判断该评论是一级还是二级
         * 一级只有 postid 就行了  其他都为空
         * 二级评论 判断是否为回复评论
         * 若不为回复评论  toCommentId 可以为空
         * 否则都不能为空
         */
        String toCommentId = commentDto.getToCommentId();
        if (!Strings.isBlank(toCommentId)){
            //回复评论要求参数都不能为空
            if (commentDto.getRootCommentId().isBlank()||commentDto.getToUserId().isBlank()||commentDto.getToCommentId().isBlank()){
                throw new RuntimeException("回复评论要求参数都不能为空。");
            }
            // 回复评论存在，该评论与它不在同一一级评论下，则不符合规定。
            // 回复评论存在，它是一级评论的话，则不符合规定。 一级评论不能回复其他评论，其他评论也不能回复一级评论
            //回复评论存在  它的uid  应该等于我的同uid
            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getPostCommentId,commentDto.getToCommentId())
                    .eq(Comment::getDetectionStatus,1);
            Comment replayComment = getOne(commentLambdaQueryWrapper);
            if (Objects.isNull(replayComment)){
                throw new RuntimeException("评论与回复评论不在同一一级评论下。");
            }
            if ((replayComment != null && (!replayComment.getRootCommentId().equals(commentDto.getRootCommentId()) )|| replayComment.getRootCommentId().equals("0"))||!commentDto.getToUserId().equals(replayComment.getUserId()))
            {
                throw new RuntimeException("参数校验出错。");
            }

        }else{
            //非回复评论
            if ((Strings.isBlank(commentDto.getRootCommentId()) && !Strings.isBlank(commentDto.getToUserId())) && (!Strings.isBlank(commentDto.getRootCommentId())&& commentDto.getToUserId().isBlank())){
                throw new RuntimeException("参数校验出错。");
            }
            //否则为二级评论  判断uid和to uid是否相同
            if (!Strings.isBlank(commentDto.getToCommentId())){
                LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                commentLambdaQueryWrapper.eq(Comment::getPostCommentId,commentDto.getToCommentId())
                        .eq(Comment::getDetectionStatus,1);
                Comment replayComment = getOne(commentLambdaQueryWrapper);
                if (Objects.isNull(replayComment)){
                    throw new RuntimeException("参数校验出错。");
                }
                if (! replayComment.getUserId().equals(commentDto.getToUserId())){
                    throw new RuntimeException("参数校验出错。");
                }
            }
        }
    }
}
