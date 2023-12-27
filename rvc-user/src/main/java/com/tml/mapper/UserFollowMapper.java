package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.UserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Date 2023/12/8
 * @Author xiaochun
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
    @Select("SELECT CASE WHEN COUNT(*) >= 1 THEN true ELSE false END AS Result " +
            "FROM rvc_user_follow WHERE ${clunme1} = ${value1} AND ${clunme2} = ${value2}")
    boolean exist(String clunme1, String value1, String clunme2, String value2);
}
