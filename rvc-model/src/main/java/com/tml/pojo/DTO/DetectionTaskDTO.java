package com.tml.pojo.DTO;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/7 23:04
 */
@Data
@Builder
public class DetectionTaskDTO implements Serializable {

    private String id;
    private String content;
    private String name;

    public static DetectionTaskDTO createDTO(String modelId, String content, String name) {
        return DetectionTaskDTO.builder()
                .id(modelId)
                .content(content)
                .name(name)
                .build();
    }

    public static AsyncDetectionForm createAsyncDetectionForm(DetectionTaskDTO dto, String type) {
        AsyncDetectionForm form = new AsyncDetectionForm();
        form.setTaskDTO(dto);
        form.setType(type);
        return form;
    }

}
