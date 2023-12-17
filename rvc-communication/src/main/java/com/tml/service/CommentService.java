package com.tml.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tml.pojo.DTO.CommentDto;
import com.tml.pojo.DTO.CommentStatusDto;
import com.tml.pojo.entity.CommentDo;

public interface CommentService  extends IService<CommentDo> {
    void comment(CommentDto commentDto);

    void status(CommentStatusDto commentStatusDto);
}
