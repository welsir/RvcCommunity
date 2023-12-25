package com.tml.domain.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @NAME: CoverDto
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/19
 */
@Data
@Builder
public class CoverDto {
    private String coverUrl;
    private String uid;
}