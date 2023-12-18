package com.tml.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tml.common.Result;
import com.tml.pojo.VO.*;
import com.tml.service.ModelService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

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

    /**
     * @description 分页获取模型列表(不分类)
     * @param page 当前页数
     * @param limit 限制条数(最大20条)
     * @param sortType 排序顺序(默认时间倒序排序)
     * @param uid 用户uid，用于显式是否点赞收藏信息
     * @return Result<?>
     */
    @GetMapping("/list")
    public Result<?> getModelList(@RequestParam("page") @NotBlank String page,
                                  @RequestParam(value = "limit",required = false) String limit,
                                  @RequestParam(value = "sortType",required = false)  String sortType,
                                  @RequestHeader(value = "uid", required = false) String uid){
        Page<ModelVO> modelList = modelService.getModelList(limit,page,sortType,uid);
        return Result.success(modelList);
    }

    /**
     * @description 分类、分页获取模型列表
     * @param typeId 分类唯一Id
     * @param page 当前页数
     * @param limit 最大
     * @param sortType 排序顺序同上
     * @param uid 同上
     * @return Result<?>
     */
    @GetMapping("/list/{typeId}")
    public Result<?> getModelListByType(@PathVariable @NotBlank String typeId,
                                        @RequestParam("page") @NotBlank String page,
                                        @RequestParam(value = "limit",required = false)  String limit,
                                        @RequestParam(value = "sortType",required = false) String sortType,
                                        @RequestHeader(value = "uid", required = false) String uid){
        Page<ModelVO> modelList = modelService.getModelList(typeId,page,limit,sortType,uid);
        return Result.success(modelList);
    }

    /**
     * @description 查看指定模型详细信息
     * @param modelId
     * @param uid
     * @return Result<?>
     */
    @GetMapping("/one/{modelId}")
    public Result<?> getOneModel(@PathVariable @NotBlank String modelId,
                                 @RequestHeader(value = "uid",required = false) String uid){
        ModelVO model = modelService.queryOneModel(modelId,uid);
        return Result.success(model);
    }

    //todo:考虑重复上传问题
    /**
     * @description:
     * @param: model
     * @param uid
     * @return: Result<?>
     **/
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

    @PostMapping("/relative/likes")
    public Result<?> modelUserLike(@RequestParam("status") String status,
                                   @RequestParam("modelId")String modelId,
                                   @RequestHeader(value = "uid") String uid){
        return Result.success(modelService.userLikesModel(status,modelId,uid));
    }
    @PostMapping("/relative/collection")
    public Result<?> modelUserCollection(@RequestParam("status") String status,
                                   @RequestParam("modelId")String modelId,
                                   @RequestHeader(value = "uid") String uid){
        return Result.success(modelService.userCollectionModel(status,modelId,uid));
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

    @DeleteMapping("/one")
    public Result<?> delOneModel(@RequestParam("id") String modelId,
                                 @RequestHeader("uid") @NotBlank String uid){
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
                                 @RequestParam("type") String type,
                                 @RequestParam("id") @NotBlank String commentId){
        return Result.success(modelService.likeComment(uid,commentId,type));
    }

    @GetMapping("/comment/first")
    public Result<?> queryFirstComments(@RequestParam("id")@NotBlank String commentId,
                                        @RequestParam(value = "limit",required = false) String limit,
                                        @RequestParam(value = "sortType",required = false) String sortType,
                                        @RequestParam("page") @NotBlank String page,
                                        @RequestHeader(value = "uid",required = false) String uid){
        return Result.success(modelService.queryFirstCommentList(commentId,page,limit,sortType,uid));
    }

    @GetMapping("/comment/second")
    public Result<?> querySecondComments(
            @RequestParam("id") @NotBlank String commentId,
            @RequestParam(value = "limit",required = false) String limit,
            @RequestParam(value = "sortType",required = false) String sortType,
            @RequestParam("page") @NotBlank String page,
            @RequestHeader(value = "uid",required = false) String uid
    ){
        return Result.success(modelService.querySecondCommentList(commentId,page,limit,sortType,uid));
    }

}
