package com.tml.designpattern.chain.ext;

import com.tml.designpattern.chain.ApproveChain;
import com.tml.mapper.post.CoverMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.tml.constant.DBConstant.RVC_COMMUNICATION_COVER;
import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_TYPE;

/**
 * @NAME: CoverExistApproveChain
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/28
 */
@RequiredArgsConstructor
@Component
public class CoverExistApproveChain extends ApproveChain {
    private final CoverMapper coverMapper;
    @Override
    public boolean approve() {
        if (coverMapper.existsRecord(RVC_COMMUNICATION_COVER,"cover_id",  getParams())){
            //下一个处理器
            return getNextChain().approve();
        }
        throw new RuntimeException("封面不存在");
    }
}