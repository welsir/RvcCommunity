package com.tml.client;

import com.tml.config.FeignConfig;
import com.tml.constant.RemoteModuleURL;
import com.tml.pojo.DTO.UserCollectionModelVO;
import com.tml.pojo.DTO.UserLikesModelVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/18 10:24
 */
@FeignClient(name = "rvc_model_service",configuration = FeignConfig.class)
public interface ModelServiceClient {

    @GetMapping(value = RemoteModuleURL.GET_USER_LIKES_MODELS,consumes = MediaType.APPLICATION_JSON_VALUE)
    List<UserLikesModelVO> getUserLikeModels(@RequestHeader("uid") String uid,
                                             @RequestParam("page")String page,
                                             @RequestParam(value = "limit",required = false)String limit,
                                             @RequestParam(value = "sortType",required = false)String sortType);

    @GetMapping(value = RemoteModuleURL.GET_USER_COLLECTION_MODELS,consumes = MediaType.APPLICATION_JSON_VALUE)
    List<UserCollectionModelVO> getUserCollectionModels(@RequestHeader("uid") String uid,
                                                        @RequestParam("page")String page,
                                                        @RequestParam(value = "limit",required = false)String limit,
                                                        @RequestParam(value = "sortType",required = false)String sortType);
}
