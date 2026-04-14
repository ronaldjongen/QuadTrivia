package com.clarityunited.quadtrivia.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record AppSecurityProperties(
        String demoUsername,
        String demoPasswordHash
) {
}
