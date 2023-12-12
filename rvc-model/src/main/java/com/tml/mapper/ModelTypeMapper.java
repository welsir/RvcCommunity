package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.ModelTypeDO;
import org.apache.ibatis.annotations.Insert;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/12 20:07
 */
public interface ModelTypeMapper extends BaseMapper<ModelTypeDO> {

    @Insert("insert into rvc_model_model_type (model_id,type_id) values(#{modelId},#{typeId})")
    int insertModelTypeRelative(String modelId,String typeId);

}
