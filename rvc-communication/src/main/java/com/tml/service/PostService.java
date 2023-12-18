package com.tml.service;

import com.tml.pojo.dto.CoinDto;
import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.dto.PostDto;
import com.tml.pojo.vo.PostSimpleVo;
import com.tml.pojo.vo.PostVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    List<PostSimpleVo> userFavorite(PageInfo<String> params);

    List<PostSimpleVo> userCollect(PageInfo<String> params);

    List<PostSimpleVo> userCreate(PageInfo<String> params);

    void updUserProfile(MultipartFile profile) throws IOException;
}
