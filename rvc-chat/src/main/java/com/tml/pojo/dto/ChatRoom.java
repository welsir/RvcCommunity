package com.tml.pojo.dto;

import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/19 15:27
 */
@Data
public class ChatRoom {

    private String id;
    private String name;
    private String picture;
    private String password;
    private List<String> userIdList = new CopyOnWriteArrayList<>();
}
