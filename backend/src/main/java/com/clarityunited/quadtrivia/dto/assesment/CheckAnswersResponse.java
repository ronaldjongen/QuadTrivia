package com.clarityunited.quadtrivia.dto.assesment;

import com.clarityunited.quadtrivia.dto.assesment.AnswerAssessmentResultDto;

import java.util.List;

public record CheckAnswersResponse(
        int score,
        int total,
        List<AnswerAssessmentResultDto> results
) {
}
