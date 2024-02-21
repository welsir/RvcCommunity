package com.tml.core.impl;

import com.tml.core.Rule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @NAME: NumberRule
 * @USER: yuech
 * @Description:
 * @DATE: 2024/2/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class NumberRule implements Rule<Integer> {

    private String rule;

    private String id;


    @Override
    public Integer ruleParser() {
        if (Objects.isNull(rule)){
            log.info("请设置参数");
            return null;
        }
        return null;
    }
}