package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.domain.DO.UserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Date 2023/12/8
 * @Author xiaochun
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
    @Select("SELECT CASE WHEN COUNT(*) >= 1 THEN true ELSE false END AS Result " +
            "FROM rvc_user_follow WHERE ${clunme} = ${value}")
    boolean exist(String clunme, String value);
}
