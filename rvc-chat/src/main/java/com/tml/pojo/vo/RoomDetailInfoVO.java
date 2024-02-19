package com.tml.pojo.vo;

import com.tml.pojo.DO.UserInfo;
import com.tml.pojo.VO.UserInfoVO;
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
    private String name;
    private List<UserInfoVO> userInfoVOS = new ArrayList<UserInfoVO>();

}
