package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.ModelUserDO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/14 12:40
 */
public interface ModelUserMapper extends BaseMapper<ModelUserDO> {

    @Select("select model_id from rvc_model_model_user where uid = #{uid}")
    List<String> selectModelIdByUid(String uid);

    @Select("<script>" +
            "select uid from rvc_model_model_user where model_id in " +
            "<foreach item='modelId' collection='modelIds' open='(' separator=',' close=')'>" +
            "#{modelId}" +
            "</foreach>" +
            "ORDER BY FIELD(model_id, " +
            "<foreach item='modelId' collection='modelIds' separator=','>" +
            "#{modelId}" +
            "</foreach>" +
            ")" +
            "</script>")
    List<String> queryUidByModelIds(List<Long> modelIds);

    @Select("select * from rvc_model_model_user where uid = #{uid} and model_id = #{modelId}")
    Object queryModelUserRelative(String uid,String modelId);
}
