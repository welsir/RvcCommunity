package com.tml.controller;

import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.annotation.apiAuth.WhiteApi;
import com.tml.exception.RvcSQLException;
import com.tml.pojo.form.FeedbackCommentForm;
import com.tml.pojo.form.FeedbackForm;
import com.tml.pojo.vo.FeedbackCommentVO;
import com.tml.pojo.vo.FeedbackVO;
import com.tml.service.FeedbackCommentService;
import com.tml.service.FeedbackService;
import com.tml.service.FeedbackStatusService;
import com.tml.service.FeedbackTypeService;
import io.github.common.PageVO;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeRegisterException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

    @Resource
    FeedbackCommentService commentService;

    @GetMapping("/list")
    @LaxTokenApi
    public Result<PageVO<FeedbackVO>> getFeedbackList(@RequestHeader(required = false)String uid,
                                                      @RequestParam @Min(1) Integer page,
                                                      @RequestParam @Min(1) @Max(42) Integer limit,
                                                      @RequestParam(required = false,value = "") String order){
        return feedbackService.getFeedbackPageVO(page, limit, uid, order);
    }

    @GetMapping("/getFeedback")
    @LaxTokenApi
    public Result<?> getFeedbackList(@RequestHeader(required = false)String uid,
                                                      @RequestParam(name = "fbid") Long fbid){
        return feedbackService.getFeedbackVO(uid,fbid);
    }

    @PostMapping("/add")
    @WhiteApi
    public Result<?> addFeedback(
            @RequestBody @Validated(value = {FeedbackForm.ADD.class}) FeedbackForm form,
            @RequestHeader String uid
    ) throws SnowflakeRegisterException {
        return feedbackService.addFeedback(form,uid);
    }

    @PostMapping("/update")
    @WhiteApi
    public Result<?> updateFeedback(
            @RequestBody @Validated(value = {FeedbackForm.UPDATE.class}) FeedbackForm form,
            @RequestHeader String uid
    ) {
        return feedbackService.updateFeedback(form,uid);
    }

    @GetMapping("/like")
    @WhiteApi
    public Result<?> likeFeedback(@RequestHeader String uid,
                                 @RequestParam Long fbId,
                                 @RequestParam Boolean isLike) throws RvcSQLException, SnowflakeRegisterException {
        return feedbackService.likeFeedback(uid,fbId,isLike);
    }

    @GetMapping("/type/list")
    public Result<?> getTypeList(){
        return Result.success(Map.of("list",typeService.queryAll()));
    }

    @GetMapping("/status/list")
    public Result<?> getStatusList(){
        return Result.success(Map.of("list",statusService.queryAll()));
    }

    @GetMapping("/comment/list")
    @LaxTokenApi
    public Result<PageVO<FeedbackCommentVO>> getCommentList(@RequestHeader(required = false)String uid,
                                                            @RequestParam Long fbid,
                                                            @RequestParam @Min(1) Integer page,
                                                            @RequestParam @Min(1) @Max(value = 42,message = "页数过大") Integer limit,
                                                            @RequestParam(required = false,value = "") String order){
        return commentService.getCommentList(fbid,uid,page,limit,order);
    }

    @PostMapping("/comment/add")
    @WhiteApi
    public Result<?> addComment(@RequestBody @Validated FeedbackCommentForm commentForm,
                                @RequestHeader String uid) throws SnowflakeRegisterException, RvcSQLException {
        return commentService.addComment(commentForm,uid);
    }

    @GetMapping("/comment/like")
    @WhiteApi
    public Result<?> likeComment(@RequestHeader String uid,
                                 @RequestParam Long cmId,
                                 @RequestParam Boolean isLike) throws RvcSQLException, SnowflakeRegisterException {
        return commentService.likeComment(cmId,uid,isLike);
    }

}
