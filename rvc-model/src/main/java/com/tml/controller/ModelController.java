package com.tml.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.annotation.apiAuth.WhiteApi;
import com.tml.common.Result;
import com.tml.pojo.VO.*;
import com.tml.service.ModelService;
import org.springframework.beans.factory.annotation.Value;
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
    @LaxTokenApi
    @GetMapping("/list")
    public Result<?> getModelList(@RequestParam("page") @NotBlank(message = "page不能为空") String page,
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
    @LaxTokenApi
    @GetMapping("/listByType")
    public Result<?> getModelListByType(@RequestParam @NotBlank(message = "id不能为空") String typeId,
                                        @RequestParam("page") @NotBlank(message = "page不能为空") String page,
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
    @LaxTokenApi
    @GetMapping("/getModelMsg")
    public Result<?> getOneModel(@RequestParam("modelId") @NotBlank(message = "id不能为空") String modelId,
                                 @RequestHeader(value = "uid",required = false) String uid){
        ModelVO model = modelService.queryOneModel(modelId,uid);
        return Result.success(model);
    }

    //todo:考虑重复上传问题
    /**
     * @description: 新建模型
     * @param: model
     * @param uid
     * @return: Result<?>
     **/
    @WhiteApi
    @PostMapping("/addModel")
    public Result<?> insertOneModel(@Validated ModelInsertVO model,
                                    @RequestHeader(value = "uid") @NotBlank(message = "id为空") String uid){
        modelService.insertOneModel(model,uid);
        return Result.success();
    }

    /**
     * @description: 下载模型文件
     * @param: modelId
     * @param uid
     * @return: Result<?>
     **/
    @WhiteApi
    @PostMapping("/download")
    public Result<?> downloadModel(@RequestParam("fileId") @NotBlank String modelId,
                                   @RequestHeader(value = "uid")@NotBlank(message = "id为空") String uid){
        return Result.success(modelService.downloadModel(modelId,uid));
    }
    @WhiteApi
    @PostMapping("/update")
    public Result<?> editModel(@Validated ModelUpdateFormVO modelUpdateFormVO,
                               @RequestHeader(value = "uid") @NotBlank(message = "id为空") String uid){
        Boolean flag = modelService.editModelMsg(modelUpdateFormVO,uid);
        return Result.success(flag);
    }
    @WhiteApi
    @PostMapping("/upload/model")
    public Result<?> uploadModel(
            MultipartFile[] file,
            @RequestHeader(value = "uid")@NotBlank(message = "id为空") String uid){
        return Result.success(modelService.uploadModel(file,uid));
    }
    @WhiteApi
    @PostMapping("/upload/image")
    public Result<?> uploadImage(
            MultipartFile file,
            @RequestHeader(value = "uid")@NotBlank(message = "id为空") String uid){
        return Result.success(modelService.uploadImage(file,uid));
    }

    @WhiteApi
    @PostMapping("/upload/audio")
    public Result<?> uploadAudio(
            MultipartFile file,
            @RequestHeader(value = "uid") @NotBlank(message = "id为空") String uid
    ){
        return Result.success(modelService.uploadAudio(file,uid));
    }
    @WhiteApi
    @PostMapping("/relative/likes")
    public Result<?> modelUserLike(@RequestParam("status") @NotBlank(message = "status为空") String status,
                                   @RequestParam("modelId")@NotBlank(message = "id为空") String modelId,
                                   @RequestHeader(value = "uid") @NotBlank(message = "id为空") String uid){
        return Result.success(modelService.userLikesModel(status,modelId,uid));
    }
    @WhiteApi
    @PostMapping("/relative/collection")
    public Result<?> modelUserCollection(@RequestParam("status") @NotBlank(message = "status为空") String status,
                                   @RequestParam("modelId")@NotBlank(message = "id为空") String modelId,
                                   @RequestHeader(value = "uid")@NotBlank(message = "id为空") String uid){
        return Result.success(modelService.userCollectionModel(status,modelId,uid));
    }
    @WhiteApi
    @PostMapping("/label")
    public Result<?> insertLabel(
            @RequestParam("label") @NotBlank(message = "label为空") String label,
            @RequestHeader(value = "uid")@NotBlank(message = "id为空") String uid
    ){
        return Result.success(modelService.insertLabel(label, uid));
    }

    /**
     * @description: 获取用户点赞列表
     * @param: uid
     * @return: Result<?>
     **/
    @WhiteApi
    @GetMapping("/likes")
    public Result<?> getUserModelLikesList(
            @RequestHeader(value = "uid") @NotBlank(message = "id为空") String uid,
            @RequestParam("page") @NotBlank(message = "page不能为空") String page,
            @RequestParam(value = "limit",required = false) String limit,
            @RequestParam(value = "order",required = false) String order
    ){
        return Result.success(modelService.getUserLikesList(uid,page,limit,order));
    }

    /**
     * @description: 获取用户收藏列表
     * @param: uid
     * @param page
     * @param limit
     * @param order
     * @return: Result<?>
     **/
    @WhiteApi
    @GetMapping("/collection")
    public Result<?> getUserModelCollectionList(
            @RequestHeader(value = "uid") @NotBlank(message = "id为空") String uid,
            @RequestParam("page") @NotBlank(message = "page不能为空") String page,
            @RequestParam(value = "limit",required = false) String limit,
            @RequestParam(value = "order",required = false) String order
    ){
        return Result.success(modelService.getUserCollectionList(uid,page,limit,order));
    }

    /**
     * @description: 删除某个模型
     * @param: modelId
     * @param uid
     * @return: Result<?>
     **/
    @WhiteApi
    @DeleteMapping("/delModel")
    public Result<?> delOneModel(@RequestParam("id") @NotBlank(message = "id为空") String modelId,
                                 @RequestHeader("uid") @NotBlank(message = "id为空") String uid){
        return Result.success(modelService.delSingleModel(modelId,uid));
    }

    @WhiteApi
    @GetMapping("user/model")
    public Result<?> queryUserModelList(@RequestHeader("uid") @NotBlank(message = "id为空") String uid,
                                        @RequestParam("page") @NotBlank(message = "page为空") String page,
                                        @RequestParam(value = "limit",required = false)String limit){
        return Result.success(modelService.queryUserModelList(uid,page,limit));
    }


    @WhiteApi
    @PostMapping("/comment")
    public Result<?> addComment(@RequestBody @Validated CommentFormVO commentFormVO,
                                     @RequestHeader("uid") @NotBlank(message = "id为空") String uid){
        return Result.success(modelService.commentModel(commentFormVO,uid));
    }

    @WhiteApi
    @PostMapping("/comment/likes")
    public Result<?> likeComment(@RequestHeader("uid") @NotBlank(message = "id为空") String uid,
                                 @RequestParam("type") @NotBlank(message = "type为空") String type,
                                 @RequestParam("id") @NotBlank(message = "id为空") String commentId){
        return Result.success(modelService.likeComment(uid,commentId,type));
    }

    @LaxTokenApi
    @GetMapping("/comment/first")
    public Result<?> queryFirstComments(@RequestParam("id") @NotBlank(message = "id为空") String modelId,
                                        @RequestParam(value = "limit",required = false) String limit,
                                        @RequestParam(value = "sortType",required = false) String sortType,
                                        @RequestParam("page") @NotBlank(message = "page为空") String page,
                                        @RequestHeader(value = "uid",required = false) String uid){
        return Result.success(modelService.queryFirstCommentList(modelId,page,limit,sortType,uid));
    }

    @LaxTokenApi
    @GetMapping("/comment/second")
    public Result<?> querySecondComments(
            @RequestParam("id") @NotBlank(message = "id为空") String commentId,
            @RequestParam(value = "limit",required = false) String limit,
            @RequestParam(value = "sortType",required = false) String sortType,
            @RequestParam("page") @NotBlank(message = "page为空") String page,
            @RequestHeader(value = "uid",required = false) String uid
    ){
        return Result.success(modelService.querySecondCommentList(commentId,page,limit,sortType,uid));
    }


    @LaxTokenApi
    @GetMapping("/label/labelHot")
    public Result<?> queryLabelList(
            @RequestHeader(value = "uid",required = false) String uid,
            @RequestParam(value = "page") String page,
            @RequestParam(value = "limit",required = false) String limit
    ){
        return Result.success(modelService.getLabelList(limit,page));
    }

    @WhiteApi
    @GetMapping("/model/modelFile")
    public Result<?> queryModelFiles(@RequestHeader(value = "uid")String uid,
                                     @RequestParam(value = "modelId")String modelId){
        return Result.success(modelService.getModelFies(modelId));
    }

}
