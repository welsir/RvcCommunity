package com.tml.controller;


import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.aop.annotation.GradeSystem;
import com.tml.aop.annotation.SystemLog;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * @NAME: UnitTestController
 * @USER: yuech
 * @Description:
 * @DATE: 2024/2/13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("level/api/test")
@Slf4j
public class UnitTestController {
    @GetMapping("/exp")
    @SystemLog(businessName = "等级增加测试")
    @LaxTokenApi
    @GradeSystem
    public Result exp(){
        return Result.success("ok");
    }
}