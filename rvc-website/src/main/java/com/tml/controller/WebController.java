package com.tml.controller;

import com.tml.pojo.WebInfoDO;
import com.tml.service.IWebInfoDaoService;
import com.tml.service.WebInfoService;
import com.tml.service.WebToolService;
import io.github.common.web.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/web")
public class WebController {

    @Resource
    WebInfoService webInfoService;

    @Resource
    WebToolService webToolService;

    /**
     * 获取网站首页信息
     * @return
     */
    @GetMapping("/info")
    public Result getWebInfo(){
        return webInfoService.getWebInfo();
    }

    /**
     * 获取网站工具列表
     * @return
     */
    @GetMapping("/tools")
    public Result getWebTools(){
        return webToolService.getToolList();
    }
}
