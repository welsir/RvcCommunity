package com.tml.core.client;

import com.tml.config.FeignConfig;
import com.tml.constant.RemoteModuleURL;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/**
 * @Description
 * @Author welsir
 * @Date 2023/12/13 23:24
 */
@FeignClient(name = "tml-user-service",configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping(value = RemoteModuleURL.GET_USERINFO,consumes = MediaType.APPLICATION_JSON_VALUE)
    Object getUserInfo(@RequestParam("uid") String uid);

}
