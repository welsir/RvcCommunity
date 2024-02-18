package com.tml.designpattern.chain;

import com.tml.client.UserServiceClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @NAME: Approve
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/25
 */
//@Accessors
//@Component
//@RequiredArgsConstructor
@Data
public abstract class ApproveChain{

    String params;

    ApproveChain nextChain;

    public void setNext(String params,ApproveChain nextChain){
        this.params = params;
        this.nextChain = nextChain;
    }

    public abstract boolean approve();
}