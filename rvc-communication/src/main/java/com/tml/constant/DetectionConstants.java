package com.tml.constant;

public class DetectionConstants {

    public static final String DETECTION_ROUTER_KEY_HEADER = "detection.";

    public static final String RES_EXCHANGE_NAME = "res.topic";
    public static final String DETECTION_EXCHANGE_NAME = "detection.topic";

    public static final String TEXT_QUEUE_NAME = "res.text";
    public static final String TEXT_ROUTER_KEY = "res.text";

    public static final String IMAGE_QUEUE_NAME = "res.image";
    public static final String IMAGE_ROUTER_KEY = "res.image";

    public static final String AUDIO_QUEUE_NAME = "res.audio";
    public static final String AUDIO_ROUTER_KEY = "res.audio";


    public static final Integer UN_DETECTION = 0;
    public static final Integer DETECTION_SUCCESS = 1;
    public static final Integer DETECTION_FAIL = 2;
    public static final Integer DETECTION_Manual = 3;

}