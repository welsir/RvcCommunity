package com.tml.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tml.common.Result;
import com.tml.pojo.DO.ModelDO;
import com.tml.pojo.VO.*;
import com.tml.service.ModelService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 13:41
 */
@RestController
@RequestMapping("/model")
@Validated
public class ModelController {

    @Resource
    ModelService modelService;

    @GetMapping("/list")
    public Result<?> getModelList(@RequestParam("page") @NotBlank String page,
                                  @RequestParam(value = "limit",required = false) String size,
                                  @RequestParam(value = "sortType",required = false)  String sortType,
                                  @RequestHeader(value = "uid", required = false) String uid){
        Page<ModelVO> modelList = modelService.getModelList(size,page,sortType,uid);
        return Result.success(modelList);
    }

    @GetMapping("/list/{typeId}")
    public Result<?> getModelListByType(@PathVariable @NotBlank String typeId,
                                        @RequestParam("page") @NotBlank String page,
                                        @RequestParam(value = "limit",required = false)  String size,
                                        @RequestParam(value = "sortType",required = false) String sortType,
                                        @RequestHeader(value = "uid", required = false) String uid){
        Page<ModelVO> modelList = modelService.getModelList(typeId,page,size,sortType,uid);
        return Result.success(modelList);
    }

    @GetMapping("/one/{modelId}")
    public Result<?> getOneModel(@PathVariable @NotBlank String modelId,
                                 @RequestHeader(value = "uid") String uid){
        ModelVO model = modelService.queryOneModel(modelId,uid);
        return Result.success(model);
    }

    //todo:考虑重复上传问题
    @PostMapping("/one")
    public Result<?> insertOneModel(@Validated ModelInsertVO model,
                                    @RequestHeader(value = "uid") String uid){
        modelService.insertOneModel(model,uid);
        return Result.success();
    }

    @PostMapping("/download/{modelId}")
    public Result<?> downloadModel(@PathVariable @NotBlank String modelId,
                                   @RequestHeader(value = "uid") String uid){
        String modelUrl = modelService.downloadModel(modelId);
        return Result.success(modelUrl);
    }

    @PostMapping("/update")
    public Result<?> editModel(@RequestBody @Validated ModelUpdateFormVO modelUpdateFormVO,
                               @RequestHeader(value = "uid") String uid){
        Boolean flag = modelService.editModelMsg(modelUpdateFormVO);
        return Result.success(flag);
    }

    @PostMapping("/upload/model")
    public Result<?> uploadModel(
            MultipartFile file,
            @RequestHeader(value = "uid") String uid){
        return Result.success(modelService.uploadModel(file));
    }

    @PostMapping("/upload/image")
    public Result<?> uploadImage(
            MultipartFile file,
            @RequestHeader(value = "uid") String uid){
        return Result.success(modelService.uploadImage(file));
    }

    @PostMapping("/relative")
    public Result<?> modelUserRelative(@RequestParam("type") String type,
                                       @RequestParam("modelId")String modelId,
                                       @RequestHeader(value = "uid") String uid,
                                       @RequestParam("status") String status){
        modelService.insertRelative(type,modelId,uid,status);
        return Result.success();
    }

    @PostMapping("/label")
    public Result<?> insertLabel(
            @RequestParam("label") String label,
            @RequestHeader(value = "uid") String uid
    ){
        return Result.success(modelService.insertLabel(label, uid));
    }

    @GetMapping("/likes")
    public Result<?> getUserModelLikesList(
            @RequestHeader(value = "uid") String uid
    ){
        return Result.success(modelService.getUserLikesList(uid));
    }

    @GetMapping("/collection")
    public Result<?> getUserModelCollectionList(
            @RequestHeader(value = "uid") String uid
    ){
        return Result.success(modelService.getUserCollectionList(uid));
    }

    @DeleteMapping("one")
    public Result<?> delOneModel(@RequestParam("id") String modelId,
                                 @RequestHeader("uid") String uid){
        return Result.success(modelService.delSingleModel(modelId));
    }

    @GetMapping("user/model")
    public Result<?> queryUserModelList(@RequestHeader("uid") String uid,
                                        @RequestParam("page") String page,
                                        @RequestParam(value = "limit",required = false)String limit){
        return Result.success(modelService.queryUserModelList(uid,page,limit));
    }

    @PostMapping("/comment")
    public Result<?> addComment(@RequestBody @Validated CommentFormVO commentFormVO,
                                     @RequestHeader("uid") String uid){
        return Result.success(modelService.commentModel(commentFormVO,uid));
    }

    @PostMapping("/comment/likes")
    public Result<?> likeComment(@RequestHeader("uid") String uid,
                                 @RequestParam("id") @NotBlank String commentId){
        return Result.success(modelService.likeComment(uid,commentId));
    }
}
