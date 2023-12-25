package com.tml.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackCommentForm {

    @NotNull(message = "replyFbId Invalid")
    private Long replyFbId;


    private Long replyCmId;

    @NotBlank(message = "comment Invalid")
    @Size(min = 1, max = 600, message = "comment length must be between 1 and 600 characters")
    private String comment;
}
