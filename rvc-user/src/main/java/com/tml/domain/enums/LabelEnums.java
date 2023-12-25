package com.tml.domain.enums;

import java.io.Serializable;

/**
 * @Date 2023/12/17
 * @Author xiaochun
 */
public enum LabelEnums implements Serializable{
    NON_LABEL("nonLabel", "正常"),
    AD("ad", "广告引流"),
    POLITICAL_CONTENT("political_content", "涉政内容"),
    PROFANITY("profanity", "辱骂内容"),
    CONTRABAND("contraband", "违禁内容"),
    SEXUAL_CONTENT("sexual_content", "色情内容"),
    VIOLENCE("violence", "暴恐内容"),
    NON_SENSE("nonsense", "无意义内容"),
    NEGATIVE_CONTENT("negative_content", "不良内容"),
    RELIGION("religion", "宗教内容"),
    CYBERBULLYING("cyberbullying", "网络暴力");
    private final String label;

    private final String means;

    LabelEnums(String label, String means) {
        this.label = label;
        this.means = means;
    }

    public String getLabel() {
        return label;
    }

    public String getMeans() {
        return means;
    }
}
