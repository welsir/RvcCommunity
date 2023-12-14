package com.tml.service;

import com.tml.pojo.dto.CoinDto;
import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.dto.PostDto;
import com.tml.pojo.vo.PostVo;

import java.util.List;

public interface PostService {
    List<PostVo> list(PageInfo<String> params,String tagId);

    PostVo details(String postId);


    String cover(String coverUrl);

    void favorite(CoinDto coinDto);

    void collection(CoinDto coinDto);

    void delete(String postId);

    void add(PostDto postDto);

    void update(PostDto postDto);

    List<PostVo> userFavorite(PageInfo<String> params);

    List<PostVo> userCollect(PageInfo<String> params);

    List<PostVo> userCreate(PageInfo<String> params);
}
