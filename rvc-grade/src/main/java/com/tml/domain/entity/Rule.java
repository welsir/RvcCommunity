package com.tml.domain.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @NAME: Rule
 * @USER: yuech
 * @Description:
 * @DATE: 2024/2/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rule {
    Integer numberRule;

    //对 numberRule 进行初始化   后续添加规则需要在此处添加初始化代码
    public Rule(JSONObject jsonObject) {
        numberRule = jsonObject.getInteger("numberRule");
    }


    public static Rule getRule(RvcLevelTask task){
        JSONObject jsonObject = JSON.parseObject(task.getRule());
        Rule rule = new Rule(jsonObject);
        return rule;
    }
}