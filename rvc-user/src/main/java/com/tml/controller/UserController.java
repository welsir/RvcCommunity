package com.tml.controller;

import com.tml.common.ListElementSize;
import com.tml.common.ListNotEmpty;
import com.tml.common.Login;
import com.tml.pojo.dto.LoginDTO;
import com.tml.pojo.dto.RegisterDTO;
import com.tml.pojo.dto.UpdatePasswordDTO;
import com.tml.pojo.dto.UserInfoDTO;
import com.tml.service.UserService;
import io.github.common.web.Result;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@RestController
@Validated
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;


    /**
     * @param loginDTO LoginDTO
     * @return {@link Result}
     */
    @PostMapping("/login")
    public Result login(@RequestBody
                            @Valid
                            LoginDTO loginDTO){
        return Result.success(userService.login(loginDTO));
    }

    @PostMapping("/logout")
    public Result logout(){
        userService.logout();
        return Result.success();
    }

    /**
     * @param registerDTO RegisterDTO
     * @return {@link Result}
     */
    @PostMapping("/register")
    public Result register(@RequestBody
                               @Valid
                               RegisterDTO registerDTO){
        return Result.success(userService.register(registerDTO));
    }

    @GetMapping("/preCode")
    public Result preCode(){
        return Result.success(userService.preCode());
    }

    /**
     * @param email String
     * @param type boolean
     * @return {@link Result}
     */
    @GetMapping("/email")
    public Result email(@RequestParam
                            @Valid
                            @NotNull(message = "邮箱不能为空")
                            @Length(min = 6, max = 30, message = "邮箱长度必须在6到30之间")
                            @Email( message = "参数必须为邮箱")
                            String email,
                        @RequestParam
                            @Valid
                            @Length(min = 4, max = 10, message = "验证码长度必须在4到6之间")
                            @NotNull(message = "前置验证码不为空")
                            String code,
                        @RequestParam
                            @Valid
                            @NotNull(message = "前置验证码标识不能为空")
                            String uuid,
                        @RequestParam
                            @NotNull(message = "验证码类型不能为空")
                            int type){
        userService.sendCode(email, code, uuid, type);
        return Result.success();
    }

    /**
     * @param uid String
     * @return {@link Result}
     */
    @GetMapping("/one")
    public Result one(@RequestParam
                          @Valid
                          @NotNull(message = "uid不能为空")
                          @Length(min = 19, max = 19, message = "uid长度必须为19")
                          String uid){
        return Result.success(userService.one(uid));
    }

    /**
     * @param uidList List<String>
     * @return {@link Result}
     */
    @PostMapping("/list")
    public Result list(@RequestBody
                           @Valid
                           @ListNotEmpty
                           @ListElementSize(min = 19, max = 19, message = "uid长度为19")
                           List<String> uidList){
        return Result.success(Map.of("userList", userService.list(uidList)));
    }

    /**
     * @param userInfoDTO UserInfoDTO
     * @return {@link Result} 待完成
     */
    @PostMapping("/update")
    @Login
    public Result update(@RequestBody
                             @Valid
                             UserInfoDTO userInfoDTO){
        userService.update(userInfoDTO);
        return Result.success();
    }

    /**
     * @param uid String
     * @return {@link Result}
     */
    @GetMapping ("/follow")
    @Login
    public Result follow(@RequestParam
                             @Valid
                             @Length(min = 19, max = 19, message = "uid长度为19")
                             @NotNull(message = "uid不能为空")
                             String uid){
        userService.follow(uid);
        return Result.success();
    }

    @PostMapping("/updatePassword")
    @Login
    public Result updatePassword(@RequestParam
                                     @Valid
                                     UpdatePasswordDTO updatePasswordDTO){
        userService.updatePassword(updatePasswordDTO);
        return Result.success();
    }

    @PostMapping("getUserInfo")
    @Login
    public Result getUserInfo(){
        return Result.success(userService.getUserInfo());
    }
}
