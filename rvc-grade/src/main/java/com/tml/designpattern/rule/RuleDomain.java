package com.tml.designpattern.rule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.tml.designpattern.chain.ApproveChain;
import com.tml.domain.dto.MqConsumerTaskDto;
import com.tml.domain.entity.Rule;
import com.tml.domain.entity.RvcLevelTask;
import io.github.common.SafeBag;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 规则发现机
 * 1，负责发现所有规则并构建成规则注册表
 * 2，负责组装规则执行链
 */
@Component
public class RuleDomain {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 规则注册表
     * 存放 ruleId 与 rule名称
     */
    private final Map<String,String> ruleMap = new HashMap<>();

    /**
     * 项目启动时初始化规则注册表
     */
    @PostConstruct
    private void ruleDomainInit(){
        Map<String, AbstractGradeRule> ruleNameAndRule = applicationContext.getBeansOfType(AbstractGradeRule.class);
        ruleNameAndRule.forEach(
                (ruleName,rule)->{
                    ruleMap.put(rule.getRuleId(),ruleName);
                }
        );
    }

    private AbstractGradeRule getRule(String ruleId){
        return applicationContext.getBean(ruleMap.get(ruleId),AbstractGradeRule.class);
    }

    public RuleChain buildChain(RvcLevelTask task, MqConsumerTaskDto taskDto){
        String ruleStr = task.getRule();
        JSONObject JsonRule = JSON.parseObject(ruleStr);
        SafeBag<RuleChain> ruleChain
                = new SafeBag<>(null);
        JsonRule.forEach(
                (ruleId,ruleValue)->{
                    AbstractGradeRule rule = getRule(ruleId);
                    RuleChain newChain = new RuleChain(taskDto, task, rule);
                    newChain.setNextChain(ruleChain.getData());
                    ruleChain.setData(newChain);
                }
        );
        return ruleChain.getData();
    }
}
