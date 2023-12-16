package com.tml.core.client;

import com.tml.common.Result;
import com.tml.config.FeignConfig;
import com.tml.constant.RemoteModuleURL;
import com.tml.pojo.DTO.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.activation.MimeType;


/**
 * @Description
 * @Author welsir
 * @Date 2023/12/13 23:24
 */
@FeignClient(name = "tml-user-service")
public interface UserServiceClient {

    @GetMapping(value = RemoteModuleURL.GET_USERINFO,consumes = MediaType.APPLICATION_JSON_VALUE)
    Result<UserInfoDTO> getUserInfo(@RequestParam("uid") String uid);

}
