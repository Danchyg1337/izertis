package com.test.izertis.controller;

import com.test.izertis.dtos.request.LoginDTO;
import com.test.izertis.dtos.response.JwtTokenDTO;
import com.test.izertis.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping
    public JwtTokenDTO login(@Valid @RequestBody LoginDTO request) {
        return authService.login(request.getUsername(), request.getPassword());
    }
}