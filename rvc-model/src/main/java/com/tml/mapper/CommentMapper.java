package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.pojo.DO.CommentDO;
import com.tml.pojo.DO.UserCommentDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

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
}
