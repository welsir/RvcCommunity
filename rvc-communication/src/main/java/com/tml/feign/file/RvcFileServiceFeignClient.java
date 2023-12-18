package com.tml.feign.file;


import com.tml.pojo.vo.CommonFileVO;
import io.github.common.web.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("file-system-service0")
@Component
public interface RvcFileServiceFeignClient {
    @PostMapping("/file/local/upload")
    Result upload(@RequestBody CommonFileVO commonFileVO);
}
