package com.tml.service;

import com.tml.domain.dto.CoinDto;
import com.tml.domain.dto.CoverDto;
import com.tml.domain.dto.PageInfo;
import com.tml.domain.dto.PostDto;
import com.tml.domain.vo.PostSimpleVo;
import com.tml.domain.vo.PostVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    List<PostVo> list(PageInfo<String> params,String tagId,String uid);

    PostVo details(String postId,String uid);


//    String cover(String coverUrl);

    void favorite(CoinDto coinDto,String uid);

    void collection(CoinDto coinDto,String uid);

    void delete(String postId,String uid);

    String add(PostDto postDto,String uid);

//    void update(PostDto postDto);

    List<PostSimpleVo> userFavorite(PageInfo<String> params,String uid);

    List<PostSimpleVo> userCollect(PageInfo<String> params,String uid);

    List<PostSimpleVo> userCreate(PageInfo<String> params,String uid);

    String updUserProfile(MultipartFile profile,String uid) throws IOException;

    String coverUrl(CoverDto coverDto);
}
