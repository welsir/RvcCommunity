package com.tml.utils;

import io.github.common.Status;

public class StatusUtil {

    public static Status ERROR_403(String msg){
        return new Status("403",msg,null);
    }
}
