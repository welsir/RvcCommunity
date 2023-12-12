package com.tml.controller;

import com.tml.pojo.dto.LoginDTO;
import com.tml.pojo.dto.RegisterDTO;
import com.tml.service.UserService;
import io.github.common.web.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    public Result email(@RequestParam String email, @RequestParam boolean type){
        userService.sendCode(email, type);
        return Result.success();
    }

    @GetMapping("/one")
    public Result one(@RequestParam String uid){
        return Result.success(userService.one(uid));
    }

    @PostMapping("/list")
    public Result list(@RequestBody List<String> uidList){
        return Result.success(userService.list(uidList));
    }
}
