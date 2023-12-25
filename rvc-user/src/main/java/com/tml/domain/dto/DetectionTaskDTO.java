package com.tml.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Date 2023/12/17
 * @Author xiaochun
 */
@Data
@Builder
public class DetectionTaskDTO implements Serializable {

    private String id;
    private String content;
    private String name;

    public static DetectionTaskDTO createDTO(String uid, String content, String name) {
        return DetectionTaskDTO.builder()
                .id(uid)
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

