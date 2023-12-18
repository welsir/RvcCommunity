package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.ModelUserDO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/14 12:40
 */
public interface ModelUserMapper extends BaseMapper<ModelUserDO> {

    @Select("select model_id from rvc_model_model_user where uid = #{uid}")
    List<String> selectModelIdByUid(String uid);

}
