package com.tml.designpattern.chain.ext;

import com.tml.client.UserServiceClient;
import com.tml.designpattern.chain.ApproveChain;
import com.tml.handler.exception.SystemException;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.tml.constant.enums.AppHttpCodeEnum.NEED_LOGIN;

/**
 * @NAME: UserExistApproveChain
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/25
 */
@RequiredArgsConstructor
@Component
public class UserExistApproveChain extends ApproveChain{

    private final UserServiceClient userServiceClient;

    @Override
    public boolean approve() {
        //对用户是否存在进行审批
        Result<Boolean> exist = null;
        try {
            exist = userServiceClient.exist(getParams());
        } catch (Exception e) {
            throw new RuntimeException("用户服务寄了");
        }
        if (exist.getCode().equals("200")){
            if (Objects.isNull(exist.getData())){
                throw new RuntimeException("用户不存在");
            }
        }else {
            throw new RuntimeException("用户不存在");
        }
        //下一个处理器
        return getNextChain().approve();
    }
}