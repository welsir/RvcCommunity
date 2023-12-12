package com.tml.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tml.common.Result;
import com.tml.pojo.VO.ModelInsertVO;
import com.tml.pojo.VO.ModelUpdateFormVO;
import com.tml.pojo.VO.ModelVO;
import com.tml.pojo.VO.SingleModel;
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

    @GetMapping("/list/{size}/{page}/{sortType}")
    public Result<?> getModelList(@PathVariable @NotBlank String page,
                                  @PathVariable  @Size(max = 10) String size,
                                  @PathVariable String sortType,
                                  @RequestHeader(value = "uid", required = false) String uid){
        Page<ModelVO> modelList = modelService.getModelList(size,page,sortType,uid);
        return Result.success(modelList);
    }

    @GetMapping("/list/{type}/{size}/{page}/{sortType}")
    public Result<?> getModelListByType(@PathVariable @NotBlank String type,
                                        @PathVariable @NotBlank String page,
                                        @PathVariable @Size(max = 10) String size,
                                        @PathVariable String sortType,
                                        @RequestHeader(value = "uid", required = false) String uid){
        Page<ModelVO> modelList = modelService.getModelList(type,page,size,sortType,uid);
        return Result.success(modelList);
    }

    @GetMapping("/one/{modelId}")
    public Result<?> getOneModel(@PathVariable @NotBlank String modelId,
                                 @RequestHeader(value = "uid", required = false) String uid){
        SingleModel model = modelService.queryOneModel(modelId,uid);
        return Result.success(model);
    }

    @PostMapping("/one")
    public Result<?> insertOneModel(@Validated ModelInsertVO model){
        modelService.insertOneModel(model);
        return Result.success();
    }

    @PostMapping("/download/{modelId}/{isOpen}")
    public Result<?> downloadModel(@PathVariable @NotBlank String modelId, @PathVariable String isOpen){
        String modelUrl = modelService.downloadModel(modelId,isOpen);
        return Result.success(modelUrl);
    }

    @PostMapping("/update")
    public Result<?> editModel(@RequestBody @Validated ModelUpdateFormVO modelUpdateFormVO){
        Boolean flag = modelService.editModelMsg(modelUpdateFormVO);
        return Result.success(flag);
    }

    @PostMapping("/upload")
    public Result<?> uploadModel(MultipartFile file){
        return Result.success(modelService.uploadModel(file));
    }

    @PostMapping("/relative")
    public Result<?> modelUserRelative(@RequestParam("type") String type,
                                       @RequestParam("modelId")String modelId,
                                       @RequestHeader(value = "uid", required = false) String uid,
                                       @RequestParam("status") String status){
        modelService.insertRelative(type,modelId,uid,status);
        return Result.success();
    }
}
