package com.tml.mq.producer.simplefactory.detectionImpl;


import com.tml.mq.producer.handler.ProducerHandler;
import com.tml.mq.producer.simplefactory.Detection;
import com.tml.pojo.dto.DetectionTaskDto;
import com.tml.utils.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @NAME: TextDetection
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/29
 */
@Component
public class TextDetection implements Detection {

//    @Autowired
//    private ProducerHandler producerHandler;

//    @Autowired
//    private ProducerHandler producerHandler;

    private String url = "11111111111111111111";
    @Override
    public void submit( Object[] args ) {
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg != null) {
                    Class<?> argClass = arg.getClass();
                    Field[] fields = argClass.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if ("content".equals(field.getName())) {
                            try {
                                String contentValue = (String) field.get(arg);

                                DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
                                        .content(contentValue)
                                        .build();
                                //在此处 上传任务到mq
                                /**
                                 * 任务封装为 ： textDetectionTaskDto
                                 */

//                                ProducerHandler.submit(textDetectionTaskDto);
                                 ProducerHandler producerHandler = BeanUtils.getBean(ProducerHandler.class);
//                                producerHandler.submit(textDetectionTaskDto);



                                System.out.println("Content value: " + contentValue);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}