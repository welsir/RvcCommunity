package com.tml.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.entity.Post;
import com.tml.pojo.vo.PostVo;

import java.util.List;

public interface PostService {
    List<PostVo> list(PageInfo<String> params);

    PostVo details(String postId);
}
