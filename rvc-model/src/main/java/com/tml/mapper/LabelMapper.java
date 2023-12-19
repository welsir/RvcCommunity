package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.LabelDO;
import com.tml.pojo.DO.ModelLabelDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/13 22:30
 */
public interface LabelMapper extends BaseMapper<LabelDO> {

    @Select("SELECT l.label FROM rvc_model_label l " +
            "JOIN rvc_model_model_label mml ON l.id = mml.label_id " +
            "WHERE mml.model_id = #{modelId}")
    List<String> selectListById(String modelId);

    @Insert("<script>\n" +
            "    INSERT INTO rvc_model_model_label (model_id, label_id)\n" +
            "    VALUES\n" +
            "    <foreach collection=\"labelIds\" item=\"labelId\" separator=\",\">\n" +
            "        (#{modelId}, #{labelId})\n" +
            "    </foreach>\n" +
            "</script>")
    void insertLabel(String modelId,List<String> labelIds);

    @Select("<script>" +
            "SELECT label FROM rvc_model_label " +
            "WHERE id IN " +
            "<foreach item='id' collection='labelsId' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<String> getLabels(List<String> labelsId);


}
