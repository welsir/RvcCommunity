package com.tml.client;

import com.alibaba.nacos.api.model.v2.Result;
import com.tml.config.FeignConfig;
import com.tml.constant.RemoteModuleURL;
import com.tml.pojo.DTO.ReceiveUploadFileDTO;
import com.tml.pojo.VO.DownloadModelForm;
import com.tml.pojo.VO.UploadModelForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/6 16:01
 */
@FeignClient(name = "file-system-service0",configuration = FeignConfig.class)
public interface FileServiceClient {

    @PostMapping(value = RemoteModuleURL.UPLOAD_FILE_TO_OSS,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<ReceiveUploadFileDTO> uploadModel(@RequestBody UploadModelForm form);

    @PostMapping(value = RemoteModuleURL.DOWNLOAD_FILE_TO_OSS,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<String> downloadModel(@RequestBody DownloadModelForm form);
}
