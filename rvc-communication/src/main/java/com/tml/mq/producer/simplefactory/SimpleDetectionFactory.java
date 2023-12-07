package com.tml.mq.producer.simplefactory;

import com.tml.mq.producer.simplefactory.detectionImpl.AudioDetection;
import com.tml.mq.producer.simplefactory.detectionImpl.ImageDetection;
import com.tml.mq.producer.simplefactory.detectionImpl.TextDetection;

/**
 * @NAME: SimpleDetectionFactory
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/29
 */
public class SimpleDetectionFactory {
    public static Detection createDetection(String type){
        //声明Coffee类型的变量，根据不同的类型创建不同的coffee子类对象
        Detection detection = null;
        if("text".equals(type)){
            detection = new TextDetection();
        }else if("image".equals(type)){
            detection = new ImageDetection();
        }else if ("audio".equals(type)){
            detection = new AudioDetection();
        }

        return detection;
    }
}