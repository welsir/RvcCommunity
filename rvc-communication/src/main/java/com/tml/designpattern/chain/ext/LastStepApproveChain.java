package com.tml.designpattern.chain.ext;

import com.tml.designpattern.chain.ApproveChain;
import org.springframework.stereotype.Component;

/**
 * @NAME: LastStepApproveChain
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/25
 */
@Component
public class LastStepApproveChain extends ApproveChain {
    @Override
    public boolean approve() {
        return true;
    }
}