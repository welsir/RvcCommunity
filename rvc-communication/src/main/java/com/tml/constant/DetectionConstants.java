package com.tml.constant;

public class DetectionConstants {

    public static final String DETECTION_ROUTER_KEY_HEADER = "detection.";

//    public static final String RES_EXCHANGE_NAME = "res.topic";
    /**
     * 审核服务常量
     * 审核服务交换机
     * 审核服务的队列名
     * 审核服务路由key
     */
    public static final String DETECTION_EXCHANGE_NAME = "res.topic";
//    public static final String DETECTION_QUEUE_NAME = "detection.topic.queue";
    public static final String DETECTION_ROUTER_KEY = "res.topic.key";


    /**
     * 处理审核常量
     * 评论队列
     * 评论路由
     */

    public static final String DETECTION_RES_COMMENT_QUEUE = "res.topic.communication.comment";
    public static final String DETECTION_RES_COMMENT_KEY = "res.topic.communication.comment.key";
    public static final String DETECTION_RES_COVER_QUEUE = "res.topic.communication.cover";
    public static final String DETECTION_RES_COVER_KEY = "res.topic.communication.cover.key";







    public static final String TEXT_QUEUE_NAME = "res.communication.text";
    public static final String TEXT_ROUTER_KEY = "res.communication.text";

    public static final String IMAGE_QUEUE_NAME = "res.communication.image";
    public static final String IMAGE_ROUTER_KEY = "res.communication.image";

    public static final String AUDIO_QUEUE_NAME = "res.communication.audio";
    public static final String AUDIO_ROUTER_KEY = "res.communication.audio";


    public static final Integer UN_DETECTION = 0;
    public static final Integer DETECTION_SUCCESS = 1;
    public static final Integer DETECTION_FAIL = 2;
    public static final Integer DETECTION_Manual = 3;




    public static final String DETECTION_TEXT_KEY = "content";
    public static final String DETECTION_IMG_KEY = "coverUrl";
    public static final String DETECTION_AUDIO_KEY = "audioUrl";




}