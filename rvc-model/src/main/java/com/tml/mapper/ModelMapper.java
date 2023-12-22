package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.ModelCollectionDO;
import com.tml.pojo.DO.ModelDO;
import com.tml.pojo.DO.ModelLikeDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 16:49
 */
public interface ModelMapper extends BaseMapper<ModelDO> {

    @Insert("INSERT INTO rvc_model_likes (model_id,uid) VALUES (#{modelId},#{uid})")
    int insertModelUserLikes(ModelLikeDO modelLikeDO);

    @Insert("INSERT INTO rvc_model_collection (model_id,uid) VALUES (#{modelId},#{uid})")
    int insertModelUserCollection(ModelCollectionDO modelCollectionDO);

    @Select("select * from rvc_model_collection where uid = #{uid} and model_id = #{modelId}")
    ModelCollectionDO queryUserModelCollection(String uid,String modelId);

    @Select("select * from rvc_model_likes where uid = #{uid} and model_id = #{modelId}")
    ModelLikeDO queryUserModelLikes(String uid,String modelId);

    @Delete("delete from rvc_model_collection where uid = #{uid} and model_id = #{modelId}")
    int delModelCollection(String uid,String modelId);

    @Delete("delete from rvc_model_likes where uid = #{uid} and model_id = #{modelId}")
    int delModelLikes(String uid,String modelId);

    @Select("select model_id from rvc_model_likes where uid = #{uid}")
    List<String> getUserLikesModel(String uid);

    @Select("select model_id from rvc_model_collection where uid = #{uid}")
    List<String> getUserCollectionModel(String uid);

    @Delete("DELETE FROM rvc_model_likes WHERE model_id = #{modelId}")
    int deleteLikesByModelId(String modelId);

    @Delete("DELETE FROM rvc_model_collection WHERE model_id = #{modelId}")
    int deleteCollectionByModelId(String modelId);

    @Insert("insert into rvc_model_model_file (model_id,index_file_id,pth_file_id,audio_file_id) values(#{modelId},#{modelId},#{modelId},#{audioId})")
    int insertModelFileRelative(String modelId,String indexId,String pthId,String audioId);
}
