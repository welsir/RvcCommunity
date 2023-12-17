package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.CommentDO;
import com.tml.pojo.DO.UserCommentDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/17 15:06
 */
public interface CommentMapper extends BaseMapper<CommentDO> {


    @Insert("INSERT INTO rvc_model_comment_user_likes (comment_id,uid) VALUES (#{commentId},#{uid})")
    int insertUserCommentRelative(String commentId,String uid);

    @Select("select * from rvc_model_comment_user_likes where uid = #{uid} and comment_id = #{commentId}")
    UserCommentDO selectDOById(String commentId,String uid);

    @Insert("insert into rvc_model_model_comment (model_id,comment_id) values(#{modelId},#{commentId})")
    int insertFirstModelComment(String modelId,String commentId);

    @Select("select comment_id from rvc_model_model_comment where model_id = #{modelId}")
    List<String> queryCommentIds(String modelId);

    @Select("select uid from rvc_model_comment where id = #{commentId}")
    String queryUidByCommentId(String commentId);

    @Delete("delete from rvc_model_comment_user_likes where uid = #{uid} and comment_id = #{commentId}")
    int delUserCommentLikes(String commentId,String uid);

    @Select("select id from rvc_model_comment where parent_id = #{firstCommentId}")
    List<String> querySecondComments(String firstCommentId);
}
