package com.tml.mapper.comment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.domain.entity.Comment;
import com.tml.domain.entity.LikeComment;
import com.tml.mapper.common.CommonMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper  extends CommonMapper<Comment> {
    void addFavorite(@Param("ew")QueryWrapper <Comment> wrapper);

    void disFavorite(@Param("ew")QueryWrapper <Comment> wrapper);

    int deleteLikeComment(@Param("ew")QueryWrapper <LikeComment> wrapper);

//    boolean existsRecord(String rvcCommunicationComment, String postCommentId, String params);
}
