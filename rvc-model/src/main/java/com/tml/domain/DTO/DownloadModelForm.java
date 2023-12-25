package com.tml.domain.DTO;

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

    private String fileId;
    private String isPrivate;
    private String bucket;

}
