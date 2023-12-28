package com.tml.mapper.post;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.domain.entity.PostType;
import com.tml.mapper.comment.CommentMapper;
import com.tml.mapper.common.CommonMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostTypeMapper extends CommonMapper<PostType> {


}
