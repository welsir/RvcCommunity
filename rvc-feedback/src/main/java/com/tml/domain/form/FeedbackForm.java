package com.tml.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackForm{

    public interface ADD{};

    public interface UPDATE{};

    @NotNull(message = "fbid Invalid",groups = {UPDATE.class})
    private Long fbid;

    @NotBlank(message = "title Invalid",groups = {ADD.class,UPDATE.class})
    @Size(min = 1, max = 40, message = "title length must be between 1 and 40 characters",groups = {ADD.class,UPDATE.class})
    private String title;

    @NotBlank(message = "content Invalid",groups = {ADD.class,UPDATE.class})
    private String content;

    @NotNull(message = "feedback type Invalid",groups = {ADD.class})
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "feedback type must be a number",groups = {ADD.class})
    private Integer type;
}
