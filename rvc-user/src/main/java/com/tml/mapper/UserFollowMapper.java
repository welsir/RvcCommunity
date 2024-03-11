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
    boolean exist(String clunme1, String value1, String clunme2, String value2);
}
