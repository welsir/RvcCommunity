package com.tml.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.api.R;
import com.tml.pojo.dto.LoginDTO;
import com.tml.pojo.dto.RegisterDTO;
import com.tml.service.UserService;
import io.github.common.web.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO){
        return Result.success(userService.login(loginDTO));
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO){
        return Result.success(userService.register(registerDTO));
    }

    @GetMapping("/email")
    public Result email(@RequestParam String email){
        return Result.success(userService.sendCode(email));
    }
}
