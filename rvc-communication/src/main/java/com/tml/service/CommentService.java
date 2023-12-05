package com.tml.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tml.pojo.dto.CommentDto;

import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.entity.Comment;

public interface CommentService  extends IService<Comment> {
    void comment(CommentDto commentDto);



    Page<Comment> list(PageInfo<String> params);

}
