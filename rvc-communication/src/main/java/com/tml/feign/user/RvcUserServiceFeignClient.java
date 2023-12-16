package com.tml.feign.user;

import com.tml.pojo.entity.UserInfoVO;
import io.github.common.web.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "tml-user-service",
        url = "1.94.28.8:9000",
        path = "/user")
//@FeignClient(name = "tml-user-service",
//        path = "/user")
public interface RvcUserServiceFeignClient {
    @GetMapping("/one")
    Result<UserInfoVO> one(@RequestParam("uid") String uid);

    @PostMapping("/list")
    Result <Map<String,List<UserInfoVO>>> list(@RequestBody List<String> uidList);
}
