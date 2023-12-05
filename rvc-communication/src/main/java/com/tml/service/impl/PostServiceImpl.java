package com.tml.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.mapper.CollectPostMapper;
import com.tml.mapper.LikePostMapper;
import com.tml.mapper.PostMapper;
import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.entity.CollectPost;
import com.tml.pojo.entity.Comment;
import com.tml.pojo.entity.LikePost;
import com.tml.pojo.entity.Post;
import com.tml.pojo.vo.PostVo;
import com.tml.service.PostService;
import com.tml.utils.BeanCopyUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @NAME: PostServiceImpl
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/5
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private CollectPostMapper collectPostMapper;
    private LikePostMapper likePostMapper;
    public PostServiceImpl(CollectPostMapper collectPostMapper,LikePostMapper likePostMapper) {
        this.collectPostMapper = collectPostMapper;
        this.likePostMapper = likePostMapper;
    }

    @Override
    public List<PostVo> list(PageInfo<String> params) {
//        模拟获取uuid
        String uuid = "1";
//        如果请求Header中uid不为null，要返回该用户对各个帖子的是否点赞和收藏


//分页获
        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();
        Page<Post> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getHasShow, 1); // hasshow 等于 1 的条件
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
}