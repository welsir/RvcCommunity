package com.tml.domain.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NotBlank
@TableName("rvc_gateway_request_record")
public class RequestRecordDO {

    private String recordId;

    private String ip;

    private String apiUrl;

    private String date;
}
