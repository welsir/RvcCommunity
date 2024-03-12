package com.tml.pojo.vo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/19 15:56
 */
@Data
public class RoomDetailInfoVO implements Serializable {

    private static final long serialVersionUID = 6821102413465632376L;

    private String roomId;
    private String ownerId;
    private String picture;
    private String name;
    private List<String> userIdList;

}
