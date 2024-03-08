package com.tml.designpattern.rule;

import com.alibaba.fastjson.JSON;
import com.tml.designpattern.chain.ApproveChain;
import com.tml.domain.dto.MqConsumerTaskDto;
import com.tml.domain.entity.RvcLevelTask;
import lombok.Builder;
import lombok.Data;

/**
 * 规则校验链
 */
@Data
public class RuleChain {

    private MqConsumerTaskDto taskDto;

    private RvcLevelTask task;

    private AbstractGradeRule rule;

    private RuleChain nextChain;


    public RuleChain(MqConsumerTaskDto taskDto, RvcLevelTask task, AbstractGradeRule rule) {
        this.taskDto = taskDto;
        this.task = task;
        this.rule = rule;
        this.rule.setTask(task);
        this.rule.setTaskDto(taskDto);
    }

    public void setNextChain(RuleChain nextChain){
        this.nextChain = nextChain;
    }

    public boolean ruleCheck(){
        rule.ruleParser(JSON.parseObject(task.getRule()));
        if(rule.check()){
            if(nextChain != null){
                return nextChain.ruleCheck();
            }
            return true;
        }
        return false;
    }

    public void lastWord(){
        rule.lastWord();
        if(nextChain != null){
             nextChain.lastWord();
        }
    }
}
