package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.domain.DO.TypeDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/14 17:15
 */
public interface TypeMapper extends BaseMapper<TypeDO> {

    @Select("select type from rvc_model_type where id = #{typeId}")
    String selectTypeById(String typeId);

    @Insert("insert into rvc_model_model_type (model_id,type_id) values(#{modelId},#{typeId})")
    int insertModelType(String modelId,String typeId);
}
