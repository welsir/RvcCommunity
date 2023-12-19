package com.tml.feign.communication;

import com.tml.pojo.dto.CoverDto;
import io.github.common.web.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("rvc-communication-service")
@Component
public interface RvcCommunicationServiceFeignClient {
    @PostMapping("/communication/post/coverUrl")
    Result coverUrl(@RequestBody CoverDto coverDto);
}
