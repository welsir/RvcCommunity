package com.tml.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Date 2023/12/8
 * @Author xiaochun
 */

@TableName("rvc_user_data")
@Data
public class UserData {
    private String uid;

    private int followNum;

    private int postNum;

    private int fansNum;

    private int modelNum;
}
