package com.example.holidayplanner.config.jwt.token;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1.0/tokens")
@Tag(name = "Token")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class TokenController {
    @Autowired
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping(path = "/refreshaccesstoken")
    public ResponseEntity refreshAccessToken(@RequestBody String refreshToken) {
        return tokenService.refreshAccessToken(refreshToken);
    }
}
