package com.clarityunited.quadtrivia.dto.quizanswer;

import jakarta.validation.constraints.NotBlank;

public record SubmittedAnswerDto(
        @NotBlank String questionId,
        @NotBlank String optionId
) {
}
