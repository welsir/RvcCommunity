package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.entity.CommentDo;
import com.tml.pojo.entity.PostTypeDo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper  extends BaseMapper<CommentDo> {
}
