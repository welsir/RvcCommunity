package com.tml.client;

import com.tml.constant.RemoteUserURL;
import com.tml.pojo.VO.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import io.github.common.web.Result;
import java.util.List;
import java.util.Map;

/**
 * @Date 2023/12/19
 * @Author xiaochun
 */
@FeignClient(name = "rvc-user-service")
public interface UserServiceClient {
    @GetMapping(value = RemoteUserURL.USER_ONE)
    Result<UserInfoVO> one(@RequestParam String uid);

    @PostMapping(value = RemoteUserURL.USER_LIST)
    Result<Map<String, List<UserInfoVO>>> list(@RequestBody List<String> uidList);

    @GetMapping(value = RemoteUserURL.USER_EXIST)
    Result exist(@RequestParam String uid);
}
