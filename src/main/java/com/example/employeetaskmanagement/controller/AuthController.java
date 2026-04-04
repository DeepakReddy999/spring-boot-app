package com.example.employeetaskmanagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeetaskmanagement.dto.LoginRequestDto;
import com.example.employeetaskmanagement.dto.TokenResponseDto;
import com.example.employeetaskmanagement.dto.UserProfileResponseDto;
import com.example.employeetaskmanagement.security.JwtTokenService;
import com.example.employeetaskmanagement.security.SecurityUtils;
import com.example.employeetaskmanagement.service.UserService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(
    name = "Authentication",
    description = "Endpoints for local demo login and for viewing the currently authenticated user."
)
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthController(
        UserService userService,
        AuthenticationManager authenticationManager,
        JwtTokenService jwtTokenService
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    // Local demo login endpoint.
    // This is intentionally simple for presentations and local testing.
    // In enterprise deployment, login would usually happen through Okta, Azure AD, or Keycloak using OAuth2/OpenID Connect.
    @PostMapping("/login")
    @Operation(
        summary = "Log in and receive a JWT token",
        description = "Authenticates a user with email and password and returns a Bearer JWT for local demo use."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        String normalizedEmail = normalizeEmail(requestDto.getEmail());
        log.info("action=login_attempt email={}", normalizedEmail);

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, requestDto.getPassword())
            );
            log.info("action=login_success email={}", authentication.getName());

            return ResponseEntity.ok(
                jwtTokenService.generateToken(userService.getUserEntityByEmail(authentication.getName()))
            );
        } catch (BadCredentialsException exception) {
            log.warn("action=login_failed email={} reason=bad_credentials", normalizedEmail);
            throw exception;
        } catch (AuthenticationServiceException exception) {
            log.warn("action=login_failed email={} reason=authentication_service_error", normalizedEmail);
            throw exception;
        } catch (RuntimeException exception) {
            log.error("action=login_failed email={} reason=token_generation_or_lookup_error", normalizedEmail, exception);
            throw exception;
        }
    }

    // Backward-compatible alias for earlier local demo flows.
    @PostMapping("/token")
    @Hidden
    public ResponseEntity<TokenResponseDto> createToken(@Valid @RequestBody LoginRequestDto requestDto) {
        return login(requestDto);
    }

    @GetMapping("/me")
    @Operation(
        summary = "Get the current user profile",
        description = "Returns profile details for the authenticated user represented by the Bearer token."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token")
    })
    public UserProfileResponseDto getCurrentUser() {
        return userService.getCurrentUserProfile(SecurityUtils.getCurrentUsername());
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
