package com.clarityunited.quadtrivia.model;

import java.util.List;

public record TriviaQuestion(
        String id,
        String text,
        List<TriviaOption> options
) {
}