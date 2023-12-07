package com.tml.pojo.form;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Builder
@Data
public class FeedbackForm{

    public interface ADD{};

    public interface UPDATE{};

    @NotBlank(message = "fbid Error",groups = {UPDATE.class})
    private String fbid;

    @NotBlank(message = "title Invalid")
    @Pattern(regexp= "^[\\d\\p{L}]{1,25}$",message = "title Error",groups = {ADD.class,UPDATE.class})
    private String title;

    @NotBlank(message = "content Invalid",groups = {ADD.class,UPDATE.class})
    private String content;

    @NotBlank(message = "feedback type Invalid")
    private Integer type;
}
