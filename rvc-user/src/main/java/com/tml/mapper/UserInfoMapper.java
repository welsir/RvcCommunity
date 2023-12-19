package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @Date 2023/12/8
 * @Author xiaochun
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    @Select("SELECT CASE WHEN COUNT(*) >= 1 THEN true ELSE false END AS Result " +
            "FROM rvc_user_info WHERE ${clunme} = '${value}'")
    boolean exist(@Param("clunme") String clunme, @Param("value") String value);
}
