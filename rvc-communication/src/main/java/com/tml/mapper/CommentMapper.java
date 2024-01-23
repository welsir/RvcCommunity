package com.tml.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.entity.Comment;
import com.tml.pojo.entity.LikeComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper  extends BaseMapper<Comment> {
    boolean existsRecord(@Param("key") String key, @Param("value") String value);

    void addFavorite(@Param("ew")QueryWrapper <Comment> wrapper);

    void disFavorite(@Param("ew")QueryWrapper <Comment> wrapper);

    int deleteLikeComment(@Param("ew")QueryWrapper <LikeComment> wrapper);
}
