package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.UserData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Date 2023/12/8
 * @Author xiaochun
 */
@Mapper
public interface UserDataMapper extends BaseMapper<UserData> {
    UserData selectByUid(@Param("uid") String uid);

    int updateFollowNum(String uid, boolean flag);

    int updateFansNum(String uid, boolean flag);
}