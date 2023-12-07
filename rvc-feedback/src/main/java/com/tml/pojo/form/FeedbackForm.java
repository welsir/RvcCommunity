package com.tml.pojo.form;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FeedbackForm{
    private String title;
    private String content;
    private Integer type;
}
