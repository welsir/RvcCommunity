package com.tml.pojo.DO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tml.pojo.vo.UserInfoVO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Date 2023/12/8
 * @Author xiaochun
 */

@TableName("rvc_user_info")
@Data
public class UserInfo {
    @TableId(value = "uid")
    private String uid;

    private String username;

    private String email;

    private String description;

    private String password;

    private String nickname;

    private String avatar;

    private String sex;

    private String phone;

    private LocalDate birthday;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime registerData;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public static UserInfoVO toVO(UserInfo userInfo){
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUid(userInfo.getUid());
        userInfoVO.setUsername(userInfo.getUsername());
        userInfoVO.setBirthday(userInfo.getBirthday());
        userInfoVO.setSex(userInfo.getSex());
        userInfoVO.setAvatar(userInfo.getAvatar());
        userInfoVO.setDescription(userInfoVO.getDescription());
        return userInfoVO;
    }
}
