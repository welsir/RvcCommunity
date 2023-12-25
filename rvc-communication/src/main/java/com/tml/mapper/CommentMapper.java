package com.tml.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.domain.entity.Comment;
import com.tml.domain.entity.LikeComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper  extends BaseMapper<Comment>,MyBaseMapper{
    void addFavorite(@Param("ew")QueryWrapper <Comment> wrapper);

    void disFavorite(@Param("ew")QueryWrapper <Comment> wrapper);

    int deleteLikeComment(@Param("ew")QueryWrapper <LikeComment> wrapper);
}
