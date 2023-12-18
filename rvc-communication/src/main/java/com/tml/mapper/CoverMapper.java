package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.entity.Comment;
import com.tml.pojo.entity.Cover;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.ManagedBean;

@Mapper
public interface CoverMapper  extends BaseMapper<Cover> {
}
