package com.tml.core.client;

import com.tml.constant.RemoteModuleURL;
import com.tml.pojo.DTO.UserRelativeRequestForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/10 21:35
 */
@FeignClient(name = "user-service")
public interface UserServiceClient {
    @PostMapping(RemoteModuleURL.IS_COLLECTION)
    String isCollection(@RequestBody UserRelativeRequestForm userRelativeRequestForm);

    @PostMapping(RemoteModuleURL.IS_LIKE)
    String isLike(@RequestBody UserRelativeRequestForm userRelativeRequestForm);
}
