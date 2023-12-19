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
    String comment(CommentDto commentDto);

    List<CommentVo> list(PageInfo<String> params);

    void favorite(CoinDto coinDto);
}
