package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.TypeDO;
import org.apache.ibatis.annotations.Select;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/14 17:15
 */
public interface TypeMapper extends BaseMapper<TypeDO> {

    @Select("select type from rvc_model_type where id = #{typeId}")
    String selectTypeById(String typeId);

}
