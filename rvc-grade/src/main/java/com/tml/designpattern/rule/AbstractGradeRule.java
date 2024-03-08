package com.tml.designpattern.rule;

import com.alibaba.fastjson.JSONObject;
import com.tml.domain.dto.MqConsumerTaskDto;
import com.tml.domain.entity.RvcLevelTask;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//等级规则抽象类
@Component
@Data
public abstract class AbstractGradeRule {

    protected RvcLevelTask task;
    protected MqConsumerTaskDto taskDto;
    public abstract String getRuleId();
    public abstract String getRuleName();

    /**
     * 规则解析器
     * 解析规则数据得到对应的值
     */
    protected abstract boolean ruleParser0(JSONObject originData);


    public boolean ruleParser(JSONObject data){
        try {
            if (data.containsKey(getRuleId())) {
                return ruleParser0(data.getJSONObject(getRuleId()));
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }
    /**
     *  规则校验
     *  判断当前事件是否符合规则，如果符合返回true 反之 返回 false
     */
    public abstract boolean check();

    /**
     * 规则后续处理事件
     */
    public abstract void lastWord();

}
