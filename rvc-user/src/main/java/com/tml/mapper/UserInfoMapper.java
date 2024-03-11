package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Date 2023/12/8
 * @Author xiaochun
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    boolean exist(String clunme, String value);

    UserInfo selectByUid(@Param("uid") String uid);

    UserInfo selectByClumneAndValue(@Param("clunme") String clunme, @Param("value") String value);
}
