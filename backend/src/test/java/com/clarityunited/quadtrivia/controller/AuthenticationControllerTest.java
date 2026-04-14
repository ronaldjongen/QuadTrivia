package com.clarityunited.quadtrivia.controller;

import com.clarityunited.quadtrivia.dto.authentication.LoginRequest;
import com.clarityunited.quadtrivia.dto.authentication.LoginResult;
import com.clarityunited.quadtrivia.exception.ApiExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationControllerTest {

    private WebTestClient webTestClient;
    private AuthenticationManager authenticationManager;
    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        controller = new AuthenticationController(authenticationManager);
        
        webTestClient = WebTestClient.bindToController(controller)
                .controllerAdvice(new ApiExceptionHandler())
                .argumentResolvers(resolvers -> {
                    resolvers.addCustomResolver(new HandlerMethodArgumentResolver() {
                        @Override
                        public boolean supportsParameter(MethodParameter parameter) {
                            return parameter.getParameterType().equals(HttpServletRequest.class);
                        }

                        @Override
                        public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
                            HttpServletRequest request = mock(HttpServletRequest.class);
                            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                            HttpSession session = mock(HttpSession.class);
                            when(request.getSession(false)).thenReturn(session);
                            return Mono.just(request);
                        }
                    });
                    resolvers.addCustomResolver(new HandlerMethodArgumentResolver() {
                        @Override
                        public boolean supportsParameter(MethodParameter parameter) {
                            return parameter.getParameterType().equals(HttpServletResponse.class);
                        }

                        @Override
                        public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
                            return Mono.just(mock(HttpServletResponse.class));
                        }
                    });
                })
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/login should return 200 on success")
    void loginShouldReturn200OnSuccess() {
        LoginRequest request = new LoginRequest("user", "pass");
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user");
        when(auth.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        webTestClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("POST /api/auth/login should return 401 on failure")
    void loginShouldReturn401OnFailure() {
        LoginRequest request = new LoginRequest("user", "wrong");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        webTestClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("GET /api/auth/me should return user info when authenticated")
    void meShouldReturnUserInfoWhenAuthenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(auth.isAuthenticated()).thenReturn(true);

        LoginResult result = controller.me(auth);
        assertEquals(new LoginResult("testuser", true), result);
    }

    @Test
    @DisplayName("GET /api/auth/me should return anonymous result when not authenticated")
    void meShouldReturnAnonymousWhenNotAuthenticated() {
        LoginResult result = controller.me(null);
        assertEquals(new LoginResult(null, false), result);
    }
}
