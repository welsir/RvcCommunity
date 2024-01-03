package com.tml.constant.enums;

import lombok.Data;

/**
 * @NAME: ContentDetectionEnum
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/18
 */

public enum ContentDetectionEnum {

    COMMENT("comment","text"),
    POST_CONTENT("post","text"),
    POST_COVER("post","image");

    private String name;
    private String type;

    ContentDetectionEnum(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getFullName(){
        return name + "." + type;
    }

    public String getRouterKey(){
        return "res.communication." + type;
    }
}