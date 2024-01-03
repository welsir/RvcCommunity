package com.tml.designpattern.chain.ext;

import com.tml.designpattern.chain.ApproveChain;
import com.tml.mapper.post.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST;

/**
 * @NAME: PostExistApproveChain
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/25
 */
@RequiredArgsConstructor
@Component
public class PostExistApproveChain extends ApproveChain {

    private final PostMapper postMapper;

    @Override
    public boolean approve() {
        if (postMapper.existsRecord(RVC_COMMUNICATION_POST,"post_id",  getParams())){
            //下一个处理器
            return getNextChain().approve();
        }
        throw new RuntimeException("帖子不存在");
    }
}