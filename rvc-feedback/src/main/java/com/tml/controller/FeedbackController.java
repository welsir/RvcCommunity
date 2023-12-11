package com.tml.controller;

import com.tml.pojo.FeedbackTypeDO;
import com.tml.pojo.form.FeedbackForm;
import com.tml.pojo.vo.FeedbackVO;
import com.tml.service.FeedbackService;
import com.tml.service.FeedbackStatusService;
import com.tml.service.FeedbackTypeService;
import io.github.common.PageVO;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeRegisterException;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/feedback")
public class FeedbackController {

    @Resource
    FeedbackService feedbackService;

    @Resource
    FeedbackTypeService typeService;

    @Resource
    FeedbackStatusService statusService;

    @GetMapping("/list")
    public Result<PageVO<FeedbackVO>> getFeedbackList(@RequestHeader(required = false)String uid,
                                                      @RequestParam Integer page,
                                                      @RequestParam Integer limit,
                                                      @RequestParam(required = false,value = "") String order){
        return feedbackService.getFeedbackPageVO(page, limit, uid, order);
    }

    @GetMapping("/getFeedback")
    public Result<?> getFeedbackList(@RequestHeader(required = false)String uid,
                                                      @RequestParam(name = "fbid") Long fbid){
        return feedbackService.getFeedbackVO(uid,fbid);
    }

    @PostMapping("/add")
    public Result<?> addFeedback(
            @RequestBody @Validated(value = {FeedbackForm.ADD.class}) FeedbackForm form,
            @RequestHeader String uid
    ) throws SnowflakeRegisterException {
        return feedbackService.addFeedback(form,uid);
    }

    @PostMapping("/update")
    public Result<?> updateFeedback(
            @RequestBody @Validated(value = {FeedbackForm.UPDATE.class}) FeedbackForm form,
            @RequestHeader String uid
    ) {
        return feedbackService.updateFeedback(form,uid);
    }

    @GetMapping("/typeList")
    public Result<?> getTypeList(){
        return Result.success(Map.of("list",typeService.queryAll()));
    }

    @GetMapping("/statusList")
    public Result<?> getStatusList(){
        return Result.success(Map.of("list",statusService.queryAll()));
    }
}
