package com.clarityunited.quadtrivia.controller;

import com.clarityunited.quadtrivia.dto.authentication.LoginRequest;
import com.clarityunited.quadtrivia.dto.authentication.LoginResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String username = request.username();
        String remoteAddress = httpRequest.getRemoteAddr();
        log.info("Login attempt for username='{}' from ip='{}'", username, remoteAddress);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            username,
                            request.password()
                    )
            );
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, httpRequest, httpResponse);
            var session = httpRequest.getSession(true);
            httpRequest.changeSessionId();
            log.info("Login successful for username='{}' sessionId='{}'",
                    authentication.getName(), session != null ? session.getId() : "none");
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            log.warn("Login failed for username='{}' from ip='{}' reason='{}'",
                    username, remoteAddress, ex.getClass().getSimpleName());
            throw ex;
        }
    }

    @GetMapping("/me")
    public LoginResult me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            log.debug("Authentication status requested for anonymous user");
            return new LoginResult(null, false);
        }

        log.debug("Authentication status requested for username='{}'", authentication.getName());
        return new LoginResult(authentication.getName(), true);
    }
}
