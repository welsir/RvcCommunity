package com.tml.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tml.domain.dto.CoinDto;
import com.tml.domain.dto.CommentDto;

import com.tml.domain.dto.PageInfo;
import com.tml.domain.entity.Comment;
import com.tml.domain.vo.CommentVo;

import java.util.List;

public interface CommentService  extends IService<Comment> {
    String comment(CommentDto commentDto,String uid);

    List<CommentVo> list(PageInfo<String> params,String uid);

    void favorite(CoinDto coinDto,String uid);

    List<CommentVo> childrenList(PageInfo<String> params,String uid);
}
