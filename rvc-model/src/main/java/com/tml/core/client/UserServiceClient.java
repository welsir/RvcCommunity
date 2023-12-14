package com.tml.core.client;

import com.tml.common.Result;
import com.tml.config.FeignConfig;
import com.tml.constant.RemoteModuleURL;
import com.tml.pojo.DTO.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/**
 * @Description
 * @Author welsir
 * @Date 2023/12/13 23:24
 */
@FeignClient(name = "tml-user-service",url = "1.94.28.8:9000")
public interface UserServiceClient {

    @GetMapping(value = RemoteModuleURL.GET_USERINFO)
    Result<UserInfoDTO> getUserInfo(@RequestParam("uid") String uid);

}
