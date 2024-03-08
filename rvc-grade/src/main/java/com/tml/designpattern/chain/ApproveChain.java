package com.tml.designpattern.chain;

import com.tml.client.UserServiceClient;
import com.tml.designpattern.rule.AbstractGradeRule;
import com.tml.domain.dto.MqConsumerTaskDto;
import com.tml.domain.entity.Rule;
import com.tml.domain.entity.RvcLevelTask;
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

    MqConsumerTaskDto taskDto;

    RvcLevelTask task;

    ApproveChain nextChain;


    public void setNext(MqConsumerTaskDto taskDto,RvcLevelTask task,ApproveChain nextChain){
        this.taskDto = taskDto;
        this.task = task;
        this.nextChain = nextChain;
    }

    public abstract boolean approve();


}
