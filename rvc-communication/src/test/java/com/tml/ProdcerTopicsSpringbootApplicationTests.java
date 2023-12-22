package com.tml;

import com.alibaba.fastjson.JSON;
import com.tml.mapper.CommentMapper;
import com.tml.mapper.CoverMapper;
import com.tml.mapper.PostMapper;
import com.tml.pojo.dto.DetectionTaskDto;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import javax.annotation.Resource;

import static com.tml.constant.DetectionConstants.DETECTION_EXCHANGE_NAME;


@SpringBootTest
@RunWith(SpringRunner.class)
class ProdcerTopicsSpringbootApplicationTests {
    @Resource
    RabbitTemplate rabbitTemplate;

    @Autowired
    private CoverMapper coverMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;


    @Test
    public void sqlTest(){
//        Post post = Post.builder()
//                .postId("1")
//                .delete(1)
//                .build();
//        postMapper.updateById(post);
    }
 
    @Test
    public void Producer_topics_springbootTest() {


 
        //使用rabbitTemplate发送消息
        String message = "你好,冰毒";
        String audioUrl = "http://downsc.chinaz.net/Files/DownLoad/sound1/201906/11582.mp3";
        String imageUrl = "https://ts1.cn.mm.bing.net/th/id/R-C.748160bf925a7acb3ba1c9514bbc60db?rik=AYY%2bJ9WcXYIMgw&riu=http%3a%2f%2fseopic.699pic.com%2fphoto%2f50017%2f0822.jpg_wh1200.jpg&ehk=CMVcdZMU6xxsjVjafO70cFcmJvD62suFC1ytk8UuAUk%3d&risl=&pid=ImgRaw&r=0";

        //违规音频
        String adurl = "https://s1.aigei.com/src/aud/mp3/da/da83cab34cd840e3a63f0be7b1d14f48.mp3?e=1702590840&token=P7S2Xpzfz11vAkASLTkfHN7Fw-oOZBecqeJaxypL:aCDF_d5dX4nAtD1uJAJhfC33Pqc=";

        /**
         * 参数：
         * 1、交换机名称
         * 2、routingKey
         * 3、消息内容
         */

        String exchangeName ="detection.topic";
        String msg = "hello";
        DetectionTaskDto audio = DetectionTaskDto.builder()
                .id("1")
                .content(adurl)
                .name("cover")
                .build();

        rabbitTemplate.convertAndSend(DETECTION_EXCHANGE_NAME, "detection." + "audio", JSON.toJSONString(audio));

    }
}