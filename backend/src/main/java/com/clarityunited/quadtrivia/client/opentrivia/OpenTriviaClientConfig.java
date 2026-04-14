package com.clarityunited.quadtrivia.client.opentrivia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class OpenTriviaClientConfig {

    @Bean
    public Duration openTriviaRequestTimeout(@Value("${app.trivia.request-timeout:5s}") Duration requestTimeout) {
        return requestTimeout;
    }

    @Bean("openTriviaWebClient")
    public WebClient openTriviaWebClient(
            @Value("${app.trivia.base-url:https://opentdb.com}") String baseUrl,
            Duration openTriviaRequestTimeout
    ) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(openTriviaRequestTimeout);

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
