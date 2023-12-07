package com.tml.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.mapper.PostTypeMapper;
import com.tml.pojo.entity.PostTypeDo;
import com.tml.service.PostTypeService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @NAME: PostTypeServiceImpl
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/28
 */
@Service
public class PostTypeServiceImpl extends ServiceImpl<PostTypeMapper, PostTypeDo> implements PostTypeService {

//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//





}