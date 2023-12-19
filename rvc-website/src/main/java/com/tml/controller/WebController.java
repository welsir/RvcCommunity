package com.tml.controller;

import com.google.protobuf.ServiceException;
import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.pojo.WebInfoDO;
import com.tml.service.*;
import io.github.common.web.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/web")
public class WebController {

    @Resource
    WebInfoService webInfoService;

    @Resource
    NoticeService noticeService;

    @Resource
    WebToolService webToolService;

    @Resource
    TeamService teamService;

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

    /**
     * 获取网站的团队人员
     * @return
     */
    @GetMapping("/team")
    public Result getWebTeam(){
        return teamService.getTeamList();
    }

    /**
     * 获取首页的公告栏
     * @return
     */
    @GetMapping("/notice/homeNotice")
    public Result homeNotice(){
        return noticeService.getHomeNoticeList();
    }

    /**
     * 获取公告页面的公告数据列表
     * @return
     */
    @GetMapping("/notice/webNotice")
    public Result webNotice(@RequestParam("page")Integer page){
        return noticeService.getWebNoticeList(page);
    }

    /**
     * 获取一个公告的详细信息
     * @param noticeId  公告ID
     * @param uid       用户ID
     * @return
     */
    @GetMapping("/notice/detail")
    @LaxTokenApi
    public Result getNotice(@RequestParam String noticeId,
                            @RequestHeader(required = false) String uid){
        return noticeService.getWebNoticeDetail(noticeId,uid);
    }

    /**
     * 增加公告浏览量，用户如果登录查看公告根据时间限制增加公告浏览量，否则不进行任何操作
     * @param noticeId
     * @param uid
     * @return
     */
    @GetMapping("/notice/watch")
    @LaxTokenApi
    public Result watchNotice(@RequestParam String noticeId,
                            @RequestHeader(required = false) String uid) throws ServiceException {
        return noticeService.watchNotice(noticeId,uid);
    }
}
