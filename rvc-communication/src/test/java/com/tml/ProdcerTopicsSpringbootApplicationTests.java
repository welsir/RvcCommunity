package com.tml;

import com.tml.mapper.CommentMapper;
import com.tml.mapper.CoverMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import javax.annotation.Resource;

import static com.tml.constant.DetectionConstants.EXCHANGE_TOPICS_INFORM;


@SpringBootTest
@RunWith(SpringRunner.class)
class ProdcerTopicsSpringbootApplicationTests {
    @Resource
    RabbitTemplate rabbitTemplate;

    @Autowired
    private CoverMapper coverMapper;

    @Autowired
    private CommentMapper commentMapper;
 
    @Test
    public void Producer_topics_springbootTest() {


 
        //使用rabbitTemplate发送消息
        String message = "你好,冰毒";
        String audioUrl = "http://downsc.chinaz.net/Files/DownLoad/sound1/201906/11582.mp3";
        String imageUrl = "https://ts1.cn.mm.bing.net/th/id/R-C.748160bf925a7acb3ba1c9514bbc60db?rik=AYY%2bJ9WcXYIMgw&riu=http%3a%2f%2fseopic.699pic.com%2fphoto%2f50017%2f0822.jpg_wh1200.jpg&ehk=CMVcdZMU6xxsjVjafO70cFcmJvD62suFC1ytk8UuAUk%3d&risl=&pid=ImgRaw&r=0";


        /**
         * 参数：
         * 1、交换机名称
         * 2、routingKey
         * 3、消息内容
         */

        String exchangeName ="detection.topic";
        String msg = "hello";
//        rabbitTemplate.convertAndSend(exchangeName, "china.news", msg);
//        rabbitTemplate.convertAndSend(EXCHANGE_TOPICS_INFORM, "inform.text", message);
//        rabbitTemplate.convertAndSend(EXCHANGE_TOPICS_INFORM, "inform.image", imageUrl);
//        rabbitTemplate.convertAndSend(EXCHANGE_TOPICS_INFORM, "inform.audio", audioUrl);

        System.out.println(coverMapper.selectById("1732366351294660608"));
        System.out.println(commentMapper.selectById("1732345167421243392"));

    }


 
 
}