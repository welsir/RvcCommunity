package com.tml.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tml.pojo.dto.CommentDto;
import com.tml.pojo.dto.CommentStatusDto;
import com.tml.pojo.entity.CommentDo;
import com.tml.pojo.entity.PostTypeDo;

public interface CommentService  extends IService<CommentDo> {
    void comment(CommentDto commentDto);

    void status(CommentStatusDto commentStatusDto);
}
