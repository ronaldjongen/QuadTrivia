package com.clarityunited.quadtrivia.dto.quizanswer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SubmittedQuizAnswerRequest(
        @NotBlank String quizId,
        @NotEmpty List<@Valid SubmittedAnswerDto> answers
) {
}
