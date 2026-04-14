package com.clarityunited.quadtrivia.client.opentrivia;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OpenTriviaQuestion(
        String category,
        String type,
        String difficulty,
        String question,
        @JsonProperty("correct_answer") String correctAnswer,
        @JsonProperty("incorrect_answers") List<String> incorrectAnswers
) {
}
