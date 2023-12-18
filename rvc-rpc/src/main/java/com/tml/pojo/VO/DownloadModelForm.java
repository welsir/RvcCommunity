package com.tml.pojo.VO;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/18 10:23
 */
@Data
@Builder
public class DownloadModelForm {

    private String fileId;
    private String isPrivate;
    private String bucket;

}
