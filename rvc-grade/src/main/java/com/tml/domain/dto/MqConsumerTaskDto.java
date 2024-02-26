package com.tml.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @NAME: MqConsumerTaskDto
 * @USER: yuech
 * @Description:
 * @DATE: 2024/2/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqConsumerTaskDto {
    String path;
    String userId;
}