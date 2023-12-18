package com.tml.pojo.DTO;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @NAME: DetectionTaskDto
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/29
 */
@Data
@Builder
public class DetectionTaskDto implements Serializable {

    private String id;
//    内容
    private String content;
<<<<<<< HEAD:rvc-communication/src/main/java/com/tml/pojo/dto/DetectionTaskDto.java

    //    业务名
    private String name;
}
=======
//    回调
    private String url;
}
>>>>>>> upgrade/master:rvc-communication/src/main/java/com/tml/pojo/DTO/DetectionTaskDto.java
