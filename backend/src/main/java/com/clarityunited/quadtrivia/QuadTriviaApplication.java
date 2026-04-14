package com.clarityunited.quadtrivia;

import com.clarityunited.quadtrivia.security.AppSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan()
public class QuadTriviaApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuadTriviaApplication.class, args);
    }

}
