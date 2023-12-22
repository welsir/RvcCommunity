package com.tml.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.client.UserServiceClient;
import com.tml.exception.SystemException;
import com.tml.mapper.CommentMapper;

import com.tml.mapper.LikeCommentMapper;
import com.tml.mapper.PostMapper;
import com.tml.pojo.VO.UserInfoVO;
import com.tml.pojo.dto.*;
import com.tml.pojo.entity.Comment;
import com.tml.pojo.entity.LikeComment;
import com.tml.pojo.entity.Post;
import com.tml.pojo.vo.CommentVo;
import com.tml.service.CommentService;
import com.tml.utils.BeanCopyUtils;
import com.tml.utils.Uuid;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tml.constant.DetectionConstants.*;
import static com.tml.enums.AppHttpCodeEnum.*;


/**
 * @NAME: CommentServiceImpl
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/30
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl  extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final CommentMapper commentMapper;
    private final LikeCommentMapper likeCommentMapper;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;

    @Override
    public String comment(CommentDto commentDto,String uid) {

        // TODO: 2023/12/21 此处的判断放到责任链中
        Object data = userServiceClient.exist(uid).getData();
        if (Objects.isNull(data)){
            throw new SystemException(NEED_LOGIN);
        }


        // 如果该评论为二级评论，查看其父评论是否存在
        String parentId = commentDto.getRootCommentId();
        if (!parentId.isBlank())
        {
            getById(parentId);
        }

        // TODO: 2023/12/21 此处的ifelse想办法优化
        String replyId = commentDto.getToCommentId();
        if (!replyId.isBlank()){
            // 如果回复评论id不为空，则不允许为一级评论（如果通过这步，说明该评论为二级评论）
            if (parentId.isBlank())
            {
                throw new SystemException(QUERY_ERROR);
            }

            // 如果回复评论存在，该评论与它不在同一一级评论下，则不符合规定。
            // 如果回复评论存在，它是一级评论的话，则不符合规定。 一级评论不能回复其他评论，其他评论也不能回复一级评论
            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getPostCommentId,replyId)
                    .eq(Comment::getDetectionStatus,1);
            Comment replayComment = getOne(commentLambdaQueryWrapper);
//               Comment replayComment = commentRepository.findByReplayUserId(replyId);
            if (replayComment != null && (!replayComment.getRootCommentId().equals(parentId) || replayComment.getRootCommentId().isBlank()))
            {
                throw new SystemException(QUERY_ERROR);
            }
        }else {
            commentDto.setToCommentId("0");
            commentDto.setRootCommentId("0");
        }


        // 获取当前时间
        // TODO: 2023/12/21 此处需要优化 太长了
        LocalDateTime currentTime = LocalDateTime.now();
        String uuid = Uuid.getUuid();
        Comment commentDo = Comment.builder()
                .postCommentId(uuid)
                .content(commentDto.getContent())
                .detectionStatus(UN_DETECTION)
                .userId(uid)
                .postId(commentDto.getPostId())
                .rootCommentId(commentDto.getRootCommentId())
                .toUserId(commentDto.getToUserId())
                .commentLikeCount(0L)
                .updateAt(currentTime)
                .createAt(currentTime)
                .build();
        save(commentDo);

        return uuid;
    }




    @Override
    public List<CommentVo> list(PageInfo<String> params,String uid) {
        /// TODO: 2023/12/21 逻辑需要优化 

        String postId = params.getData();
        LambdaUpdateWrapper<Post> postQueryWrapper = new LambdaUpdateWrapper<>();
        postQueryWrapper.eq(Post::getPostId, postId)
                .eq(Post::getHasDelete,0)
                .eq(Post::getDetectionStatus,1);
        if (postMapper.selectCount(postQueryWrapper) == 0){
            //post不存在
            throw new SystemException(POST_ERROR);
        }


        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();
//先获取根评论   再获取所有根评论的子评论
        Page<Comment> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, postId)
                .eq(Comment::getDetectionStatus, DETECTION_SUCCESS)
                .eq(Comment::getRootCommentId,"0");
        Page<Comment> list = this.page(page,queryWrapper);

        //所有的父评论   夫评论只有用户id  没有回复人的id
        List<Comment> records = list.getRecords();

        List<String> collect = records.stream()
                .map(record -> record.getUserId())
                .collect(Collectors.toList());

        Map<String, UserInfoVO> data = null;
        try {
            data = userServiceClient.list(collect).getData();
        } catch (Exception e) {
            throw new RuntimeException("用户服务调用异常");
        }

        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(records, CommentVo.class);


        for (int i = 0; i < commentVos.size(); i++) {
            CommentVo commentVo = commentVos.get(i);
            commentVo.setLike(false);
            if (!Strings.isBlank(uid)){
                LambdaUpdateWrapper<LikeComment> likeCommentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                likeCommentLambdaUpdateWrapper.eq(LikeComment::getUid, uid)
                        .eq(LikeComment::getCommentId, commentVo.getPostCommentId());
                LikeComment likeComment = likeCommentMapper.selectOne(likeCommentLambdaUpdateWrapper);
                if (!Objects.isNull(likeComment)){
                    commentVo.setLike(true);
                }
            }


            commentVo.setUser(data.get(records.get(i).getUserId()));

        }

//        List<String> collect = records.stream()
//                .map(comment -> comment.getPostCommentId())
//                .collect(Collectors.toList());
//
//        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
//        commentQueryWrapper.in("root_comment_id", collect);
//
//        //所有的子评论
//        List<Comment> comments = commentMapper.selectList(commentQueryWrapper);
//
//
//        List<String> sonUserIds = comments.stream()
//                .flatMap(son -> Stream.of(son.getUserId(), son.getToUserId()))
//                .collect(Collectors.toList());
//
//        Result<Map<String, UserInfoVO>> sonUser = userServiceClient.list(sonUserIds);
//
//
//        Map<String, Comment> commentMap = comments.stream()
//                .collect(Collectors.toMap(Comment::getRootCommentId, c -> c));
//
//
//        ArrayList<CommentVo> commentVos = new ArrayList<>();
//        for (int i = 0; i < records.size(); i++) {
//            CommentVo commentVo = BeanCopyUtils.copyBean(records.get(i), CommentVo.class);
//            commentVo.setUser(sonUser.getData().get(records.get(i).getUserId()));
//        }

//        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(records, CommentVo.class);

//        //        获取每一个评论的子评论  (3条)
//        for (int i = 0; i < commentVos.size(); i++) {
//            CommentVo commentVo = commentVos.get(i);
//
//            String userId = records.get(i).getUserId();
//            UserInfoVO data = userServiceClient.one(userId).getData();
//            commentVo.setUser(data);
//
//
//            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            commentLambdaQueryWrapper.eq(Comment::getRootCommentId,commentVo.getPostCommentId())
//                    .eq(Comment::getRootCommentId,commentVo.getPostCommentId())
//                    .eq(Comment::getDetectionStatus, DETECTION_SUCCESS);
//            List<Comment> comments = commentMapper.selectList(commentLambdaQueryWrapper);
//            List<CommentVo> childrenCommentVos = BeanCopyUtils.copyBeanList(comments, CommentVo.class);
//            for (int j = 0; j < childrenCommentVos.size(); j++) {
//                CommentVo childrenCommentVo = childrenCommentVos.get(i);
//                String userId1 = comments.get(j).getUserId();
//                childrenCommentVo.setUser(userServiceClient.one(userId1).getData());
//                String userId2 = comments.get(j).getToUserId();
//                childrenCommentVo.setReplayUser(userServiceClient.one(userId2).getData());
//            }
//            commentVo.setChildrenComment(childrenCommentVos);
//        }

        return commentVos;
    }

    @Override
    @Transactional
    public void favorite(CoinDto coinDto,String uid) {
        // TODO: 2023/12/21 责任链 

        Object data = userServiceClient.exist(uid).getData();
        if (Objects.isNull(data)){
            throw new SystemException(NEED_LOGIN);
        }

//        Comment comment = commentMapper.selectById(coinDto.getId());
//        if (Objects.isNull(comment)){
//            throw new SystemException(COMMENT_ERROR);
//        }
        if (!commentMapper.existsRecord("post_comment_id",coinDto.getId())){
            throw new SystemException(COMMENT_ERROR);
        }

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

    @Override
    public List<CommentVo> childrenList(PageInfo<String> params,String uid) {
        String rootCommentId = params.getData();
        /**
         *         父评论通过审核
         */

        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();

        Page<Comment> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootCommentId, rootCommentId)
                .eq(Comment::getDetectionStatus, DETECTION_SUCCESS)
                .eq(Comment::getRootCommentId,rootCommentId);
        Page<Comment> list = this.page(page,queryWrapper);

        //所有的父评论   夫评论只有用户id  没有回复人的id
        List<Comment> records = list.getRecords();

        List<String> sonUserIds = records.stream()
                .flatMap(son -> Stream.of(son.getUserId(), son.getToUserId()))
                .collect(Collectors.toList());

        Map<String, UserInfoVO> data = null;
        try {
            data = userServiceClient.list(sonUserIds).getData();
        } catch (Exception e) {
            throw new RuntimeException("用户服务调用异常");
        }

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
}
