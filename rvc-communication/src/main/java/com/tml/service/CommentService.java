package com.tml.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tml.domain.dto.CoinDto;
import com.tml.domain.dto.CommentDto;

import com.tml.domain.dto.PageInfo;
import com.tml.domain.entity.Comment;
import com.tml.domain.vo.CommentVo;

import java.util.List;


public interface CommentService  {
    String comment(CommentDto commentDto,String uid);

    List<CommentVo> list(String uid,String postId,Integer pageNum,Integer pageSize,String oder);

    void favorite(CoinDto coinDto,String uid);

    List<CommentVo> childrenList(String uid,String postId,Integer pageNum,Integer pageSize,String oder);


}
