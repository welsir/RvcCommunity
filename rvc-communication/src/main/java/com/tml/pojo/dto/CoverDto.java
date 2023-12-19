package com.tml.pojo.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

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