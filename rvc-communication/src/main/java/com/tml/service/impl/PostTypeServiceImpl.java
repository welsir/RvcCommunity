package com.tml.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.mapper.post.PostTypeMapper;
import com.tml.domain.entity.PostType;
import com.tml.service.PostTypeService;
import com.tml.utils.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Stack;

import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_TYPE;

/**
 * @NAME: PostTypeServiceImpl
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/28
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings({"all"})
public class PostTypeServiceImpl extends ServiceImpl<PostTypeMapper, PostType> implements PostTypeService {


    private final RedisCache redisCache;
//
    public List<Object> listType(){
        //从redis获取数据
        List<Object> cacheList = redisCache.getCacheList(RVC_COMMUNICATION_POST_TYPE);

        return cacheList;
    }

}