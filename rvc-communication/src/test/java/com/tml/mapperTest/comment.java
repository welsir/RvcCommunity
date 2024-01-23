
package com.tml.mapperTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tml.mapper.CommentMapper;
import com.tml.pojo.dto.CoinDto;
import com.tml.pojo.entity.Comment;
import com.tml.pojo.entity.LikeComment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @NAME: comment
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/21
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class comment {
    @Autowired
    private CommentMapper commentMapper;


    @Test
    public void exist(){
        System.out.println(commentMapper.existsRecord("post_comment_id","1737545008459939841"));
    }


    @Test
    public void addUpdate(){
        CoinDto coinDto = new CoinDto();
        coinDto.setId("1737470824128053248");
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("post_comment_id", coinDto.getId());
        commentMapper.addFavorite(commentQueryWrapper);
    }

    @Test
    public void deleteCommentLike(){
        QueryWrapper<LikeComment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("uid", "1")
                .eq("comment_id","1733403665420648448");
        System.out.println(commentMapper.deleteLikeComment(commentQueryWrapper));
    }

}