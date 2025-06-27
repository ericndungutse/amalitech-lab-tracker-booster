package com.ndungutse.project_tracker.controller;

import com.ndungutse.project_tracker.dto.LoginRequest;
import com.ndungutse.project_tracker.dto.LoginResponse;
import com.ndungutse.project_tracker.dto.RegisterRequest;
import com.ndungutse.project_tracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register", description = "Registers a new user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        LoginResponse loginResponse = authService.register(registerRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);

    }

//    @GetMapping("/oauth2/google/redirect")
//    public String redirectToGoogle(HttpServletResponse response) throws IOException {
//        System.out.println("redirectToGoogle");
//        return "done";
//    }
}