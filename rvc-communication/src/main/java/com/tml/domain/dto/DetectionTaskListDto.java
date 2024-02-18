package com.tml.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class DetectionTaskListDto {


    boolean sync = false;

    List<DetectionTaskDto> taskList;

    public DetectionTaskListDto() {
    }

    public DetectionTaskListDto(List<DetectionTaskDto> taskList) {
        this.taskList = taskList;
    }
}