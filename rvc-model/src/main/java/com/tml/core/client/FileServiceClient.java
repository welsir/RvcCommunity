package com.tml.core.client;

import com.tml.common.Result;
import com.tml.config.FeignConfig;
import com.tml.constant.RemoteModuleURL;
import com.tml.pojo.DTO.DownloadModelForm;
import com.tml.pojo.DTO.ModelDownloadDTO;
import com.tml.pojo.DTO.ReceiveUploadModelDTO;
import com.tml.pojo.DTO.UploadModelForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/6 16:01
 */
@FeignClient(name = "file-system-service0",configuration = FeignConfig.class)
public interface FileServiceClient {

    @PostMapping(value = RemoteModuleURL.UPLOAD_FILE_TO_OSS,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<ReceiveUploadModelDTO> uploadModel(@RequestBody UploadModelForm form);

    @PostMapping(value = RemoteModuleURL.DOWNLOAD_FILE_TO_OSS,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<String> downloadModel(@RequestBody DownloadModelForm form);

}
