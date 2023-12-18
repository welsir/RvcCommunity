package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.entity.LikeComment;
import com.tml.pojo.entity.LikePost;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeCommentMapper  extends BaseMapper<LikeComment> {
}
