package com.tml.pojo.DTO;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/12 22:15
 */
@Data
@Builder
public class DownloadModelForm {

    private String modelId;
    private String isPrivate;

}
