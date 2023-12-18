package com.tml.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.feign.file.RvcFileServiceFeignClient;
import com.tml.feign.user.RvcUserServiceFeignClient;
import com.tml.interceptor.UserLoginInterceptor;
import com.tml.mapper.*;
import com.tml.mq.handler.ProducerHandler;
import com.tml.pojo.dto.*;
import com.tml.pojo.entity.*;
import com.tml.pojo.vo.CommonFileVO;
import com.tml.pojo.vo.PostSimpleVo;
import com.tml.pojo.vo.PostVo;
import com.tml.service.PostService;
import com.tml.strategy.SortStrategy;
import com.tml.strategy.impl.LikeSortStrategy;
import com.tml.strategy.impl.TimeSortStrategy;
import com.tml.strategy.impl.ViewSortStrategy;
import com.tml.utils.*;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_TYPE;
import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_WATCH;
import static com.tml.constant.DetectionConstants.DETECTION_SUCCESS;
import static com.tml.constant.DetectionConstants.UN_DETECTION;

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
    private final RvcUserServiceFeignClient rvcUserServiceFeignClient;
    private final RedisCache redisCache;
    private final PostTypeMapper postTypeMapper;
    private final RvcFileServiceFeignClient rvcFileServiceFeignClient;

    private final Map<String, SortStrategy> strategyMap = new HashMap<>();
    {
        strategyMap.put("1", new TimeSortStrategy());
        strategyMap.put("2", new LikeSortStrategy());
        strategyMap.put("3", new ViewSortStrategy());
    }


    @Override
    public List<PostVo> list(PageInfo<String> params,String tagId) {
        /**
         * 1、分页获取数据（如果有tagId 添加条件）
         * 2、调用用户服务获取作者信息
         * 3、从redis获取tag信息
         * 4、将所有信息封装
         * 5、如果用户登录去关系表查询 是否关联的信息
         * 6、排序返回结果
         */

        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String uuid = loginInfoDTO.getId();

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
        Result<Map<String, List<UserInfoVO>>> res = rvcUserServiceFeignClient.list(userIds);
        List<UserInfoVO> userList = res.getData().get("userList");

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
            postVo.setAuthor(userList.get(i));
            //插入tag
            postVo.setPostType(postTypeCollect.get(records.get(i).getTagId()));
            //如果uuid 不为空 去关系表进行查询 是否点赞和收藏
        if (!Strings.isBlank(uuid)){
                LambdaQueryWrapper<CollectPost> collectPostQueryWrapper = new LambdaQueryWrapper<>();
                collectPostQueryWrapper.eq(CollectPost::getUid, uuid)
                        .eq(CollectPost::getPostId,postVo.getPostId());
                LambdaQueryWrapper<LikePost> likePostQueryWrapper = new LambdaQueryWrapper<>();
                likePostQueryWrapper.eq(LikePost::getUid, uuid)
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
    public PostVo details(String postId) {
        /**
         * 1、判断要查询的帖子是否存在
         * 2、调用用户服务获取作者信息
         * 3、从redis获取tag信息
         * 4、如果用户未登录 直接返回结果    否则异步浏览次数+1
         * 5、用户登录  返回 关联信息
         */

        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String uuid = loginInfoDTO.getId();


        Post post = this.getById(postId);
        if (Objects.isNull(post)){
            throw new RuntimeException("帖子不存在");
        }
        PostVo postVo = BeanCopyUtils.copyBean(post, PostVo.class);

        //获取作者和tag
        Object data = rvcUserServiceFeignClient.one(post.getUid()).getData();
        UserInfoVO userInfoVO = JSON.parseObject(JSON.toJSONString(data), UserInfoVO.class);
        postVo.setAuthor(userInfoVO);

        PostType postType = postTypeMapper.selectById(post.getTagId());
        postVo.setPostType(postType);


//        如果用户未登录直接返回vo对象
        if (Objects.isNull(uuid)){
            return postVo;
        }


//        异步 帖子浏览次数+1（1分钟内浏览 浏览次数不加1 并且更新上次浏览时间）
        this.executor.execute(() -> watchPost(uuid,postId));

        //去关系表查看用户是否点赞  收藏
        LambdaQueryWrapper<CollectPost> collectPostQueryWrapper = new LambdaQueryWrapper<>();
        collectPostQueryWrapper.eq(CollectPost::getUid,uuid);
        LambdaQueryWrapper<LikePost> likePostQueryWrapper = new LambdaQueryWrapper<>();
        likePostQueryWrapper.eq(LikePost::getUid,uuid);
        boolean collect = collectPostMapper.selectCount(collectPostQueryWrapper) >0;
        boolean   like =   likePostMapper.selectCount(  likePostQueryWrapper) >0;

//        封装vo对象并返回
        postVo.setLike(like);
        postVo.setCollect(collect);

        return postVo;
    }

    @Override
    public String cover(String coverUrl) {
        /**
         * 需要改
         * 不走前端上传文件
         */

        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String uuid = loginInfoDTO.getId();

//        数据库添加记录
        String coverId = Uuid.getUuid();
        Cover cover = Cover.builder()
                .coverId(coverId)
                .detectionStatus(UN_DETECTION)
                .coverUrl(coverUrl)
                .uid(uuid)
                .build();
        coverMapper.insert(cover);


        //       提交审核任务
        DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
                .id(coverId)
                .content(coverUrl)
                .name("post_cover")
                .build();

        ProducerHandler producerHandler = BeanUtils.getBean(ProducerHandler.class);
        producerHandler.submit(textDetectionTaskDto,"image");

        return coverId;
    }

    @Override
    public void favorite(CoinDto coinDto) {
        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String uid = loginInfoDTO.getId();

        /**
         * 判断用户是否存在
         */
        //帖子是否存在
        Post post = postMapper.selectById(coinDto.getId());
        if (Objects.isNull(post)){
            throw new RuntimeException("帖子不存在");
        }

        //1、点赞    添加关系表中的记录       post表 like_num +1
        //0、取消点赞    删除关系表中的记录       post表 like_num -1
        if (coinDto.getType().equals("1")){
            String uuid = Uuid.getUuid();
            LikePost likePost = new LikePost(uuid,  coinDto.getId(),uid);
            try {
                likePostMapper.insert(likePost);
            } catch (Exception e) {
                throw new RuntimeException("不允许重复点赞");
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
                  throw new RuntimeException("不允许重复取消点赞");
              }
            LambdaUpdateWrapper<Post> updateWrapper = Wrappers.<Post>lambdaUpdate()
                    .eq(Post::getPostId, coinDto.getId())
                    .setSql("like_num = like_num - 1");
            postMapper.update(null,updateWrapper);
        }
    }

    @Override
    public void collection(CoinDto coinDto) {
        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String uid = loginInfoDTO.getId();
        /**
         * 判断用户和评论是否存在
         */
        //帖子是否存在
        Post post = postMapper.selectById(coinDto.getId());
        if (Objects.isNull(post)){
            throw new RuntimeException("帖子不存在");
        }
        //1、收藏    添加关系表中的记录       post表 colletc_num +1
        //0、取消收藏    删除关系表中的记录       post表 colletc_num -1
        if (coinDto.getType().equals("1")){
            String uuid = Uuid.getUuid();
            CollectPost collectPost = new CollectPost(coinDto.getId(), uid, uuid);
            try {
                collectPostMapper.insert(collectPost);
            } catch (Exception e) {
                throw new RuntimeException("不允许重复收藏");
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
                throw new RuntimeException("操作失败");
            }
            LambdaUpdateWrapper<Post> updateWrapper = Wrappers.<Post>lambdaUpdate()
                    .eq(Post::getPostId, coinDto.getId())
                    .setSql("collect_num = collect_num - 1");
            postMapper.update(null,updateWrapper);
        }
    }

    @Override
    public void delete(String postId) {

        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String uuid = loginInfoDTO.getId();

        //帖子是否存在
        Post DBpost = postMapper.selectById(postId);
        if (Objects.isNull(DBpost)){
            throw new RuntimeException("帖子不存在");
        }


        Post post1 = postMapper.selectById(postId);
//帖子不属于该用户
        if (!post1.getUid().equals(uuid)){
            throw new RuntimeException("无权限");
        }

        Post post = Post.builder()
                .postId(postId)
                .hasDelete(1)
                .build();

        try {
            postMapper.updateById(post);
        } catch (Exception e) {
            throw new RuntimeException("记录不存在");
        }
    }

    @Override
    public void add(PostDto postDto) {
/**
 * 模拟获取userid
 */
        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String userid = loginInfoDTO.getId();

        String uuid = Uuid.getUuid();
        Post post = BeanCopyUtils.copyBean(postDto, Post.class);
        post.setPostId(uuid);
        post.setUid(userid);

        //判断tagid是否存在
        PostType postType = postTypeMapper.selectById(post.getTagId());
        if (Objects.isNull(postType)){
            throw new RuntimeException("tag不存在");
        }

        //判断cover是否存在 并且审核通过
        Cover dbCover = coverMapper.selectById(post.getCoverId());
        if (Objects.isNull(dbCover)){
            throw new RuntimeException("Cover不存在");
        }
        if (dbCover.getDetectionStatus() == 2){
            throw new RuntimeException("违规封面");
        }

        save(post);

        //更新cover表映射
        Cover cover = Cover.builder()
                .postId(uuid)
                .coverId(post.getCoverId())
                .build();

        int i = coverMapper.updateById(cover);

        //提交审核
        DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
                .id(uuid)
                .content(post.getContent())
                .name("post.text")
                .build();
        ProducerHandler producerHandler = BeanUtils.getBean(ProducerHandler.class);
        producerHandler.submit(textDetectionTaskDto,"text");
    }

    @Override
    public void update(PostDto postDto) {

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
    }

    @Override
    public List<PostSimpleVo> userFavorite(PageInfo<String> params) {

        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String uuid = loginInfoDTO.getId();

        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();
        Page<LikePost> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<LikePost> likePostLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likePostLambdaQueryWrapper.eq(LikePost::getUid, uuid);
        Page<LikePost> likePostPage = likePostMapper.selectPage(page, likePostLambdaQueryWrapper);
        List<LikePost> records = likePostPage.getRecords();
        if (records.size() == 0){
            throw new RuntimeException("无喜欢");
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
            postSimpleVo.setContent(content.substring(0, 200));
            postSimpleVo.setPostType(postTypeCollect.get(posts.get(i).getTagId()));
        }

        return postSimpleVos;
    }

    @Override
    public List<PostSimpleVo> userCollect(PageInfo<String> params) {

        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String uuid = loginInfoDTO.getId();

        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();
        Page<CollectPost> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<CollectPost> likePostLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likePostLambdaQueryWrapper.eq(CollectPost::getUid, uuid);
        Page<CollectPost> likePostPage = collectPostMapper.selectPage(page, likePostLambdaQueryWrapper);


        List<CollectPost> records = likePostPage.getRecords();
        if (records.size() == 0){
            throw new RuntimeException("无收藏");
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
            postSimpleVo.setContent(content.substring(0, 200));
            postSimpleVo.setPostType(postTypeCollect.get(posts.get(i).getTagId()));
        }

        return postSimpleVos;
    }

    @Override
    public List<PostSimpleVo> userCreate(PageInfo<String> params) {
        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String uuid = loginInfoDTO.getId();

        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();
        Page<Post> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Post> likePostLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likePostLambdaQueryWrapper.eq(Post::getUid, uuid);
        Page<Post> likePostPage = postMapper.selectPage(page, likePostLambdaQueryWrapper);
        List<Post> records = likePostPage.getRecords();
        if (records.size() == 0){
            throw new RuntimeException("无创建");
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
            postSimpleVo.setContent(content.substring(0, 200));
            postSimpleVo.setPostType(postTypeCollect.get(posts.get(i).getTagId()));
        }

        return postSimpleVos;
    }

    @Override
    public void updUserProfile(MultipartFile profile) throws IOException {

        //获取文件的byte信息
        byte[] uploadBytes = profile.getBytes();
        CommonFileVO build = CommonFileVO.builder()
                .md5(MD5Util.getMD5(uploadBytes.toString()))
                .file(profile).build();
        Result upload = rvcFileServiceFeignClient.upload(build);
        System.out.println(upload);
        System.out.println(upload.getData());

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