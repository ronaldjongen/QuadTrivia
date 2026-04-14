package com.clarityunited.quadtrivia.dto.authentication;

public record LoginResult(
        String username,
        boolean isAuthenticated
) {
}
