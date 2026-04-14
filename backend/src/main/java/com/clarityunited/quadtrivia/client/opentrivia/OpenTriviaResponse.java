package com.clarityunited.quadtrivia.client.opentrivia;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OpenTriviaResponse(
        @JsonProperty("response_code") int responseCode,
        @JsonProperty("results") List<OpenTriviaQuestion> results
) {
}
