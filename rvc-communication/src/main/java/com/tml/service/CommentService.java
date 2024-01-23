package com.tml.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tml.pojo.dto.CoinDto;
import com.tml.pojo.dto.CommentDto;

import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.entity.Comment;
import com.tml.pojo.vo.CommentVo;

import java.util.List;

public interface CommentService  extends IService<Comment> {
    String comment(CommentDto commentDto,String uid);

    List<CommentVo> list(PageInfo<String> params,String uid);

    void favorite(CoinDto coinDto,String uid);

    List<CommentVo> childrenList(PageInfo<String> params,String uid);
}
