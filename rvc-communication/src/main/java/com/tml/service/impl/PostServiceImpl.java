package com.tml.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.client.FileServiceClient;
import com.tml.client.UserServiceClient;
import com.tml.exception.SystemException;
import com.tml.interceptor.UserLoginInterceptor;
import com.tml.mapper.*;
import com.tml.pojo.DTO.ReceiveUploadFileDTO;
import com.tml.pojo.VO.UploadModelForm;
import com.tml.pojo.VO.UserInfoVO;
import com.tml.pojo.dto.*;
import com.tml.pojo.entity.*;
import com.tml.pojo.vo.PostSimpleVo;
import com.tml.pojo.vo.PostVo;
import com.tml.service.PostService;
import com.tml.designpattern.strategy.SortStrategy;
import com.tml.designpattern.strategy.impl.LikeSortStrategy;
import com.tml.designpattern.strategy.impl.TimeSortStrategy;
import com.tml.designpattern.strategy.impl.ViewSortStrategy;
import com.tml.utils.*;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import static com.tml.constant.CommonConstant.IMG_TYPE_LIST;
import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_TYPE;
import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_WATCH;
import static com.tml.constant.DetectionConstants.DETECTION_SUCCESS;
import static com.tml.constant.DetectionConstants.UN_DETECTION;
import static com.tml.enums.AppHttpCodeEnum.*;

/**
 * @NAME: PostServiceImpl
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/5
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final CollectPostMapper collectPostMapper;
    private final LikePostMapper likePostMapper;
    private final CoverMapper coverMapper;
    private final PostMapper postMapper;
    private final ThreadPoolTaskExecutor executor;
    private final UserServiceClient rvcUserServiceFeignClient;
    private final RedisCache redisCache;
    private final PostTypeMapper postTypeMapper;
    private final FileServiceClient fileServiceClient;
    private final UserServiceClient userServiceClient;

    private final Map<String, SortStrategy> strategyMap = new HashMap<>();
    {
        strategyMap.put("1", new TimeSortStrategy());
        strategyMap.put("2", new LikeSortStrategy());
        strategyMap.put("3", new ViewSortStrategy());
    }


    @Override
    public List<PostVo> list(PageInfo<String> params,String tagId,String uid) {
// TODO: 2023/12/21 责任链
        if (!Strings.isBlank(uid)){
            Object data = userServiceClient.exist(uid).getData();
            if (Objects.isNull(data)){
                throw new SystemException(NEED_LOGIN);
            }
        }

// TODO: 2023/12/21 代码优化
        //分页
        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();
        Page<Post> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getDetectionStatus, DETECTION_SUCCESS)  // 审核通过
                .eq(Post::getHasDelete,0);       //没有被删除
        //tagId 不为空  更具tagId 查询
        if (!Strings.isBlank(tagId)){
            queryWrapper.eq(Post::getTagId,tagId);
        }
        Page<Post> list = this.page(page,queryWrapper);
        //获取的分页结果
        List<Post> records = list.getRecords();
        List<PostVo> postVos = BeanCopyUtils.copyBeanList(records, PostVo.class);

        //        //获取作者
        List<String> userIds = records.stream()
                .map(post -> post.getUid())
                .collect(Collectors.toList());
        Result<Map<String, UserInfoVO>> res = rvcUserServiceFeignClient.list(userIds);

        Map<String, UserInfoVO> data = res.getData();



        //获取tag
        List<Object> cacheList = redisCache.getCacheList(RVC_COMMUNICATION_POST_TYPE);
        String jsonString = JSON.toJSONString(cacheList);
        List<PostType> postTypes = JSONArray.parseArray(jsonString, PostType.class);
        Map<String, PostType> postTypeCollect = postTypes.stream()
                .collect(Collectors.toMap(postType -> postType.getId(), postType -> postType));


        //遍历postvo
            //插入作者
            //插入tag
            //如果uuid 不为空 去关系表进行查询 是否点赞和收藏

        for (int i=0;i<postVos.size();i++){
            PostVo postVo = postVos.get(i);
            //插入作者
            postVo.setAuthor(data.get(records.get(i).getUid()));
            //插入tag
            postVo.setPostType(postTypeCollect.get(records.get(i).getTagId()));
            //如果uuid 不为空 去关系表进行查询 是否点赞和收藏
        if (!Strings.isBlank(uid)){
                LambdaQueryWrapper<CollectPost> collectPostQueryWrapper = new LambdaQueryWrapper<>();
                collectPostQueryWrapper.eq(CollectPost::getUid, uid)
                        .eq(CollectPost::getPostId,postVo.getPostId());
                LambdaQueryWrapper<LikePost> likePostQueryWrapper = new LambdaQueryWrapper<>();
                likePostQueryWrapper.eq(LikePost::getUid, uid)
                        .eq(LikePost::getPostId,postVo.getPostId());

                boolean collect = collectPostMapper.selectCount(collectPostQueryWrapper) > 0;
                boolean like = likePostMapper.selectCount(likePostQueryWrapper) > 0;
                postVo.setLike(like);
                postVo.setCollect(collect);

            }
        }

        //         排序
        SortStrategy sortStrategy = strategyMap.get(params.getData());
        sortStrategy.sort(postVos);


        return postVos;

    }

    @Override
    public PostVo details(String postId,String uid) {
// TODO: 2023/12/21 责任链

        if (!Strings.isBlank(uid)){
            Object data = userServiceClient.exist(uid).getData();
            if (Objects.isNull(data)){
                throw new SystemException(NEED_LOGIN);
            }
        }

        LambdaQueryWrapper<Post> postLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postLambdaQueryWrapper.eq(Post::getHasDelete,0)
                .eq(Post::getPostId,postId)
                .eq(Post::getDetectionStatus,1);
        Post post = this.getOne(postLambdaQueryWrapper);

        //条件  是否删除  是否违规


        if (Objects.isNull(post)){
            throw new SystemException(POST_ERROR);
        }
        PostVo postVo = BeanCopyUtils.copyBean(post, PostVo.class);

        // TODO: 2023/12/21 sql优化

        //获取作者和tag 还有cover
        Object data = rvcUserServiceFeignClient.one(post.getUid()).getData();
        UserInfoVO userInfoVO = JSON.parseObject(JSON.toJSONString(data), UserInfoVO.class);
        postVo.setAuthor(userInfoVO);

        PostType postType = postTypeMapper.selectById(post.getTagId());
        postVo.setPostType(postType);

        Cover cover = coverMapper.selectById(post.getCoverId());
        postVo.setCover(cover.getCoverUrl());


//        如果用户未登录直接返回vo对象
        if (Objects.isNull(uid)){
            return postVo;
        }


//        异步 帖子浏览次数+1（1分钟内浏览 浏览次数不加1 并且更新上次浏览时间）
        this.executor.execute(() -> watchPost(uid,postId));

        // TODO: 2023/12/21 sql优化
        //去关系表查看用户是否点赞  收藏
        LambdaQueryWrapper<CollectPost> collectPostQueryWrapper = new LambdaQueryWrapper<>();
        collectPostQueryWrapper.eq(CollectPost::getUid,uid)
                .eq(CollectPost::getPostId,postId);
        LambdaQueryWrapper<LikePost> likePostQueryWrapper = new LambdaQueryWrapper<>();
        likePostQueryWrapper.eq(LikePost::getUid,uid)
                .eq(LikePost::getPostId,postId);
        boolean collect = collectPostMapper.selectCount(collectPostQueryWrapper) >0;
        boolean   like =   likePostMapper.selectCount(  likePostQueryWrapper) >0;

//        封装vo对象并返回
        postVo.setLike(like);
        postVo.setCollect(collect);

        return postVo;
    }

//    @Override
//    public String cover(String coverUrl) {
//        /**
//         * 需要改
//         * 不走前端上传文件
//         */
//
//        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
//        String uuid = loginInfoDTO.getId();
//
////        数据库添加记录
//        String coverId = Uuid.getUuid();
//        Cover cover = Cover.builder()
//                .coverId(coverId)
//                .detectionStatus(UN_DETECTION)
//                .coverUrl(coverUrl)
//                .uid(uuid)
//                .build();
//        coverMapper.insert(cover);
//
//
//        //       提交审核任务
//        DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
//                .id(coverId)
//                .content(coverUrl)
//                .name("post_cover")
//                .build();
//
//        ProducerHandler producerHandler = BeanUtils.getBean(ProducerHandler.class);
//        producerHandler.submit(textDetectionTaskDto,"image");
//
//        return coverId;
//    }

    @Override
    @Transactional
    public void favorite(CoinDto coinDto,String uid) {



        Object data = userServiceClient.exist(uid).getData();
        if (Objects.isNull(data)){
            throw new SystemException(NEED_LOGIN);
        }




        //帖子是否存在
        Post post = postMapper.selectById(coinDto.getId());
        if (Objects.isNull(post)){
            throw new SystemException(POST_ERROR);
        }

        //1、点赞    添加关系表中的记录       post表 like_num +1
        //0、取消点赞    删除关系表中的记录       post表 like_num -1
        if (coinDto.getType().equals("1")){
            String uuid = Uuid.getUuid();
            LikePost likePost = new LikePost(uuid,  coinDto.getId(),uid);
            try {
                likePostMapper.insert(likePost);
            } catch (Exception e) {
                throw new SystemException(FAVORITE_ERROR);
            }
            LambdaUpdateWrapper<Post> updateWrapper = Wrappers.<Post>lambdaUpdate()
                    .eq(Post::getPostId, coinDto.getId())
                    .setSql("like_num = like_num + 1");
            postMapper.update(null,updateWrapper);
        } else if (coinDto.getType().equals("0")) {
            LambdaQueryWrapper<LikePost> likePostLambdaQueryWrapper = new LambdaQueryWrapper<>();
            likePostLambdaQueryWrapper.eq(LikePost::getUid,uid)
                            .eq(LikePost::getPostId,coinDto.getId());
            int delete = likePostMapper.delete(likePostLambdaQueryWrapper);
              if (delete == 0){
                  throw new SystemException(SYSTEM_ERROR);
              }
            LambdaUpdateWrapper<Post> updateWrapper = Wrappers.<Post>lambdaUpdate()
                    .eq(Post::getPostId, coinDto.getId())
                    .setSql("like_num = like_num - 1");
            postMapper.update(null,updateWrapper);
        }else {
            throw new SystemException(TYPE_ERROR);
        }
    }

    @Override
    @Transactional
    public void collection(CoinDto coinDto,String uid) {

        Object data = userServiceClient.exist(uid).getData();
        if (Objects.isNull(data)){
            throw new SystemException(NEED_LOGIN);
        }



        //帖子是否存在
        Post post = postMapper.selectById(coinDto.getId());
        if (Objects.isNull(post)){
            throw new SystemException(POST_ERROR);
        }
        //1、收藏    添加关系表中的记录       post表 colletc_num +1
        //0、取消收藏    删除关系表中的记录       post表 colletc_num -1
        if (coinDto.getType().equals("1")){
            String uuid = Uuid.getUuid();
            CollectPost collectPost = new CollectPost(coinDto.getId(), uid, uuid);
            try {
                collectPostMapper.insert(collectPost);
            } catch (Exception e) {
                throw new SystemException(COLLECT_ERROR);
            }
            LambdaUpdateWrapper<Post> updateWrapper = Wrappers.<Post>lambdaUpdate()
                    .eq(Post::getPostId, coinDto.getId())
                    .setSql("collect_num = collect_num + 1");
            postMapper.update(null,updateWrapper);
        } else if (coinDto.getType().equals("0")) {
            LambdaQueryWrapper<CollectPost> likePostLambdaQueryWrapper = new LambdaQueryWrapper<>();
            likePostLambdaQueryWrapper.eq(CollectPost::getUid,uid)
                    .eq(CollectPost::getPostId,coinDto.getId());
            int delete = collectPostMapper.delete(likePostLambdaQueryWrapper);
            if (delete == 0){
                throw new SystemException(SYSTEM_ERROR);
            }
            LambdaUpdateWrapper<Post> updateWrapper = Wrappers.<Post>lambdaUpdate()
                    .eq(Post::getPostId, coinDto.getId())
                    .setSql("collect_num = collect_num - 1");
            postMapper.update(null,updateWrapper);
        }else {
            throw new SystemException(TYPE_ERROR);
        }
    }

    @Override
    public void delete(String postId,String uid) {
        // TODO: 2023/12/21 责任链
        Object data = userServiceClient.exist(uid).getData();
        if (Objects.isNull(data)){
            throw new SystemException(NEED_LOGIN);
        }



        //帖子是否存在
        Post DBpost = postMapper.selectById(postId);
        if (Objects.isNull(DBpost)){
            throw new SystemException(POST_ERROR);
        }


        Post post1 = postMapper.selectById(postId);
//帖子不属于该用户
        if (!post1.getUid().equals(uid)){
            throw new SystemException(PERMISSIONS_ERROR);
        }

        Post post = Post.builder()
                .postId(postId)
                .hasDelete(1)
                .build();

        try {
            postMapper.updateById(post);
        } catch (Exception e) {
            throw new SystemException(POST_ERROR);
        }
    }

    @Override
    @Transactional
    public String add(PostDto postDto,String uid) {
        // TODO: 2023/12/21 责任链

        Object data = userServiceClient.exist(uid).getData();
        if (Objects.isNull(data)){
            throw new SystemException(NEED_LOGIN);
        }

        String uuid = null;
        if (Strings.isBlank(postDto.getPostId())){
            uuid = Uuid.getUuid();
        }else {
            uuid = postDto.getPostId();
        }

        Post post = BeanCopyUtils.copyBean(postDto, Post.class);
        post.setPostId(uuid);
        post.setUid(uid);

        //判断tagid是否存在
        PostType postType = postTypeMapper.selectById(post.getTagId());
        if (Objects.isNull(postType)){
            throw new SystemException(TAG_ERROR);
        }

        //判断cover是否存在 并且审核通过    ？？？并且是这个用户的
        Cover dbCover = coverMapper.selectById(post.getCoverId());
        if (Objects.isNull(dbCover)){
            throw new SystemException(COVER_ERROR);
        }
        if (dbCover.getDetectionStatus() == 2){
            throw new SystemException(DETECTION_ERROR);
        }

        //判断 post 是不是你的


        saveOrUpdate(post);

        //更新cover表映射
        Cover cover = Cover.builder()
                .postId(uuid)
                .coverId(postDto.getCoverId())
                .build();

        int i = coverMapper.updateById(cover);


        return uuid;

//        //提交审核
//        DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
//                .id(uuid)
//                .content(post.getContent())
//                .name("post.text")
//                .build();
//        ProducerHandler producerHandler = BeanUtils.getBean(ProducerHandler.class);
//        producerHandler.submit(textDetectionTaskDto,"text");
    }

//    @Override
//    public void update(PostDto postDto) {

//        String uuid = Uuid.getUuid();
//        Post post = BeanCopyUtils.copyBean(postDto, Post.class);
//        post.setPostId(uuid);
//        save(post);
//
//        //更新cover表映射
//        Cover cover = Cover.builder()
//                .postId(uuid)
//                .coverId(post.getCoverId())
//                .build();
//
//        coverMapper.updateById(cover);
//
//
//
//        //对内容进行审核
//        //        审核
//        DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
//                .id(uuid)
//                .content(post.getContent())
//                .name("post.text")
//                .build();
//
//        ProducerHandler producerHandler = BeanUtils.getBean(ProducerHandler.class);
//        producerHandler.submit(textDetectionTaskDto,"text");
//    }

    @Override
    public List<PostSimpleVo> userFavorite(PageInfo<String> params,String uid) {

        // TODO: 2023/12/21 责任链
        Object data = userServiceClient.exist(uid).getData();
        if (Objects.isNull(data)){
            throw new SystemException(NEED_LOGIN);
        }


        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String uuid = loginInfoDTO.getId();
// TODO: 2023/12/21 代码优化
        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();
        Page<LikePost> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<LikePost> likePostLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likePostLambdaQueryWrapper.eq(LikePost::getUid, uuid);
        Page<LikePost> likePostPage = likePostMapper.selectPage(page, likePostLambdaQueryWrapper);
        List<LikePost> records = likePostPage.getRecords();
        if (records.size() == 0){
            throw new SystemException(NOT_FAVORITE_ERROR);
        }


        List<String> collect = likePostPage.getRecords().stream()
                .map(likePost -> likePost.getPostId())
                .collect(Collectors.toList());

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("post_id",collect);
        List<Post> posts = postMapper.selectList(queryWrapper);

        List<PostSimpleVo> postSimpleVos = BeanCopyUtils.copyBeanList(posts, PostSimpleVo.class);

        //获取tag
        List<Object> cacheList = redisCache.getCacheList(RVC_COMMUNICATION_POST_TYPE);
        String jsonString = JSON.toJSONString(cacheList);
        List<PostType> postTypes = JSONArray.parseArray(jsonString, PostType.class);
        Map<String, PostType> postTypeCollect = postTypes.stream()
                .collect(Collectors.toMap(postType -> postType.getId(), postType -> postType));

        //对帖子内容进行限制（200字以内）
        for (int i=0;i<postSimpleVos.size();i++){
            PostSimpleVo postSimpleVo = postSimpleVos.get(i);
            String content = postSimpleVo.getContent();
            if (content.length()>200){
                postSimpleVo.setContent(content.substring(0, 200));
            }

            postSimpleVo.setPostType(postTypeCollect.get(posts.get(i).getTagId()));
        }

        return postSimpleVos;
    }

    @Override
    public List<PostSimpleVo> userCollect(PageInfo<String> params,String uid) {
// TODO: 2023/12/21 以下三个接口提取公共部分 代码优化

        Object data = userServiceClient.exist(uid).getData();
        if (Objects.isNull(data)){
            throw new SystemException(NEED_LOGIN);
        }

        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();
        Page<CollectPost> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<CollectPost> likePostLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likePostLambdaQueryWrapper.eq(CollectPost::getUid, uid);
        Page<CollectPost> likePostPage = collectPostMapper.selectPage(page, likePostLambdaQueryWrapper);


        List<CollectPost> records = likePostPage.getRecords();
        if (records.size() == 0){
            throw new SystemException(NOT_COLLECT_ERROR);
        }

        List<String> collect = likePostPage.getRecords().stream()
                .map(likePost -> likePost.getPostId())
                .collect(Collectors.toList());

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("post_id",collect);
        List<Post> posts = postMapper.selectList(queryWrapper);



        List<PostSimpleVo> postSimpleVos = BeanCopyUtils.copyBeanList(posts, PostSimpleVo.class);

        //获取tag
        List<Object> cacheList = redisCache.getCacheList(RVC_COMMUNICATION_POST_TYPE);
        String jsonString = JSON.toJSONString(cacheList);
        List<PostType> postTypes = JSONArray.parseArray(jsonString, PostType.class);
        Map<String, PostType> postTypeCollect = postTypes.stream()
                .collect(Collectors.toMap(postType -> postType.getId(), postType -> postType));

        //对帖子内容进行限制（200字以内）
        for (int i=0;i<postSimpleVos.size();i++){
            PostSimpleVo postSimpleVo = postSimpleVos.get(i);
            String content = postSimpleVo.getContent();
            if (content.length()>200){
                postSimpleVo.setContent(content.substring(0, 200));
            }
            postSimpleVo.setPostType(postTypeCollect.get(posts.get(i).getTagId()));
        }

        return postSimpleVos;
    }

    @Override
    public List<PostSimpleVo> userCreate(PageInfo<String> params,String uid) {

        Object data = userServiceClient.exist(uid).getData();
        if (Objects.isNull(data)){
            throw new SystemException(NEED_LOGIN);
        }


        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();
        Page<Post> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Post> likePostLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likePostLambdaQueryWrapper.eq(Post::getUid, uid);
        Page<Post> likePostPage = postMapper.selectPage(page, likePostLambdaQueryWrapper);
        List<Post> records = likePostPage.getRecords();
        if (records.size() == 0){
            throw new SystemException(NOT_CREATE_ERROR);
        }


        List<String> collect = likePostPage.getRecords().stream()
                .map(likePost -> likePost.getPostId())
                .collect(Collectors.toList());

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("post_id",collect);
        List<Post> posts = postMapper.selectList(queryWrapper);

        List<PostVo> postVos = BeanCopyUtils.copyBeanList(posts, PostVo.class);

        List<PostSimpleVo> postSimpleVos = BeanCopyUtils.copyBeanList(posts, PostSimpleVo.class);

        //获取tag
        List<Object> cacheList = redisCache.getCacheList(RVC_COMMUNICATION_POST_TYPE);
        String jsonString = JSON.toJSONString(cacheList);
        List<PostType> postTypes = JSONArray.parseArray(jsonString, PostType.class);
        Map<String, PostType> postTypeCollect = postTypes.stream()
                .collect(Collectors.toMap(postType -> postType.getId(), postType -> postType));

        //对帖子内容进行限制（200字以内）
        for (int i=0;i<postSimpleVos.size();i++){
            PostSimpleVo postSimpleVo = postSimpleVos.get(i);
            String content = postSimpleVo.getContent();
            if (content.length()>200){
                postSimpleVo.setContent(content.substring(0, 200));
            }
            postSimpleVo.setPostType(postTypeCollect.get(posts.get(i).getTagId()));
        }

        return postSimpleVos;
    }

    @Override
    public String updUserProfile(MultipartFile profile,String uid) throws IOException {

        Object res = userServiceClient.exist(uid).getData();
        if (Objects.isNull(res)){
            throw new SystemException(NEED_LOGIN);
        }

        //判断后缀名
        String fileSuffix = profile.getOriginalFilename().substring(profile.getOriginalFilename().lastIndexOf("."));

        if (!Arrays.stream(IMG_TYPE_LIST).anyMatch(
                type -> fileSuffix.equals(type))
        ){
            throw new SystemException(TYPE_ERROR);
        }


        byte[] uploadBytes = profile.getBytes();

        UploadModelForm build = UploadModelForm.builder()
                .bucket("rvc1")
                .file(profile)
                .md5(MD5Util.getMD5(uploadBytes.toString()))
                .path("rvc/image3")
                .build();
        com.tml.pojo.Result<ReceiveUploadFileDTO> receiveUploadFileDTOResult = null;
        try {
            receiveUploadFileDTOResult = fileServiceClient.uploadModel(build);
        } catch (Exception e) {
            throw new SystemException(SERVICE_ERROR);
        }
        ReceiveUploadFileDTO data = receiveUploadFileDTOResult.getData();

        return data.getUrl();
    }

    @Override
    public String coverUrl(CoverDto coverDto) {
        String uuid = Uuid.getUuid();
        Cover build = Cover.builder()
                .uid(coverDto.getUid())
                .coverUrl(coverDto.getCoverUrl())
                .createAt(LocalDate.now())
                .detectionStatus(UN_DETECTION)
                .coverId(uuid)
                .build();
        coverMapper.insert(build);
        return uuid;
    }


    public void watchPost(String userId,String postId){

//        从redis获取上次浏览时间
        Object cacheObject = redisCache.getCacheObject(RVC_COMMUNICATION_POST_WATCH + ":" + userId);
        if (Objects.isNull(cacheObject)){
//            如果没有获取到数据 说明是第一次浏览  保存标记到redis 设置失效时间为一小时  浏览次数直接+1
            redisCache.setCacheObject(RVC_COMMUNICATION_POST_WATCH + ":" + userId,1,1, TimeUnit.HOURS);
//            浏览量加1
            LambdaUpdateWrapper<Post> updateWrapper = Wrappers.<Post>lambdaUpdate()
                    .eq(Post::getPostId, postId)
                    .setSql("watch_num = watch_num + 1");
            postMapper.update(null,updateWrapper);
            return;
        }

//    不为空更新redis失效时间
        redisCache.setCacheObject(RVC_COMMUNICATION_POST_WATCH + ":" + userId,1,1, TimeUnit.HOURS);
            /**
             * 是否需要记录浏览信息
             */
        }
//        if (now.compareTo(oneHourBefore))
}
