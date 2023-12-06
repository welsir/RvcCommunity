package com.tml.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.mapper.CollectPostMapper;
import com.tml.mapper.CoverMapper;
import com.tml.mapper.LikePostMapper;
import com.tml.mapper.PostMapper;
import com.tml.mq.producer.handler.ProducerHandler;
import com.tml.pojo.dto.DetectionTaskDto;
import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.entity.*;
import com.tml.pojo.vo.PostVo;
import com.tml.service.PostService;
import com.tml.strategy.SortStrategy;
import com.tml.strategy.impl.LikeSortStrategy;
import com.tml.strategy.impl.TimeSortStrategy;
import com.tml.strategy.impl.ViewSortStrategy;
import com.tml.utils.BeanCopyUtils;
import com.tml.utils.BeanUtils;
import com.tml.utils.Uuid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final Map<String, SortStrategy> strategyMap = new HashMap<>();
    {
        strategyMap.put("1", new TimeSortStrategy());
        strategyMap.put("2", new LikeSortStrategy());
        strategyMap.put("3", new ViewSortStrategy());
    }


    @Override
    public List<PostVo> list(PageInfo<String> params,String tagId) {
//        模拟获取uuid
        String uuid = "1";
//        如果请求Header中uid不为null，要返回该用户对各个帖子的是否点赞和收藏


//分页获
        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();
        Page<Post> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getHasShow, 1); // hasshow 等于 1 的条件
        //tagId 不为空
        if (!Strings.isBlank(tagId)){
            queryWrapper.eq(Post::getTagId,tagId);
        }
        Page<Post> list = this.page(page,queryWrapper);
//获取的分页结果
        List<Post> records = list.getRecords();

//对帖子内容进行限制（200字以内）
        List<Post> truncatedPosts = records.stream()
                .peek(post -> {
                    String content = post.getContent();
                    if (content != null && content.length() > 200) {
                        post.setContent(content.substring(0, 200));
                    }
                })
                .collect(Collectors.toList());

        List<PostVo> postVos = BeanCopyUtils.copyBeanList(truncatedPosts, PostVo.class);
//如果uuid 不为空 去关系表进行查询 是否点赞和收藏
        if (!Strings.isBlank(uuid)){
            postVos.stream().forEach(postVo -> {
                LambdaQueryWrapper<CollectPost> collectPostQueryWrapper = new LambdaQueryWrapper<>();
                collectPostQueryWrapper.eq(CollectPost::getUid,uuid);
                LambdaQueryWrapper<LikePost> likePostQueryWrapper = new LambdaQueryWrapper<>();
                likePostQueryWrapper.eq(LikePost::getUid,uuid);

                boolean collect = collectPostMapper.selectCount(collectPostQueryWrapper) >0;
                boolean   like =   likePostMapper.selectCount(  likePostQueryWrapper) >0;
                postVo.setLike(like);
                postVo.setCollect(collect);
            });
        }

//        排序
        SortStrategy sortStrategy = strategyMap.get(params.getData());
        sortStrategy.sort(postVos);

//        获取 作者用户名  作者昵称  作者头像
//        调用用户接口

        return postVos;
    }

    @Override
    public PostVo details(String postId) {
        String userId = "1";

        Post post = this.getById(postId);

//        帖子浏览次数+1

//        去关系表查看用户是否点赞  收藏

        PostVo postVo = BeanCopyUtils.copyBean(post, PostVo.class);

        return postVo;
    }

    @Override
    public String cover(String coverUrl) {
//        数据库添加记录
        String uuid = Uuid.getUuid();
        Cover cover = Cover.builder()
                .coverId(uuid)
                .hasShow(2)
                .coverUrl(coverUrl)
                .build();
        coverMapper.insert(cover);


        //       提交审核任务
        DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
                .id(uuid)
                .content(coverUrl)
                .name("post_cover")
                .build();

        ProducerHandler producerHandler = BeanUtils.getBean(ProducerHandler.class);
        producerHandler.submit(textDetectionTaskDto,"image");

        return uuid;
    }

}