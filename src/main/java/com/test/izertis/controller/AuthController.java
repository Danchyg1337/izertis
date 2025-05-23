package com.test.izertis.controller;

import com.test.izertis.dto.request.LoginDTO;
import com.test.izertis.dto.response.JwtTokenDTO;
import com.test.izertis.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login with credentials", security = @SecurityRequirement(name = ""))
    @PostMapping
    public ResponseEntity<JwtTokenDTO> login(@Valid @RequestBody LoginDTO request) {
        return ResponseEntity.ok(authService.login(request.getUsername(), request.getPassword()));
    }
}