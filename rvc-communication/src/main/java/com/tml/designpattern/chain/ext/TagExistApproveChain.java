package com.tml.designpattern.chain.ext;

import com.tml.designpattern.chain.ApproveChain;
import com.tml.domain.entity.PostType;
import com.tml.mapper.post.PostTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST;
import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_TYPE;

/**
 * @NAME: TagExistApproveChain
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/28
 */
@RequiredArgsConstructor
@Component
public class TagExistApproveChain extends ApproveChain {
    private final PostTypeMapper postTypeMapper;
    @Override
    public boolean approve() {
        if (postTypeMapper.existsRecord(RVC_COMMUNICATION_POST_TYPE,"tag_id",  getParams())){
            //下一个处理器
            return getNextChain().approve();
        }
        throw new RuntimeException("帖子类型不存在");
    }
}