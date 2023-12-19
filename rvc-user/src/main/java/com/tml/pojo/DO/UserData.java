package com.tml.pojo.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Date 2023/12/8
 * @Author xiaochun
 */

@TableName("rvc_user_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    private String uid;

    private int followNum;

    private int postNum;

    private int fansNum;

    private int modelNum;
}
