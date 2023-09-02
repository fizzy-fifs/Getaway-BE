package com.example.holidayplanner.config.jwt.token;

import com.example.holidayplanner.config.MyUserDetailsService;
import com.example.holidayplanner.config.jwt.JwtUtil;
import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {
    @Autowired
    private final TokenRepository tokenRepository;

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    private final ObjectMapper mapper;

    public TokenService(TokenRepository tokenRepository, JwtUtil jwtUtil, MyUserDetailsService myUserDetailsService) {
        this.tokenRepository = tokenRepository;
        this.jwtUtil = jwtUtil;
        this.myUserDetailsService = myUserDetailsService;
        this.mapper = new ObjectMapper().findAndRegisterModules();
    }

    public Token saveToken(Token token) {
        return tokenRepository.save(token);
    }

    public Token findByOwner(User user) {
        return tokenRepository.findByOwner(user);
    }

    public Token findByAccessToken(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken);
    }

    public Token findByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken);
    }

    public void deleteToken(Token token) {
        tokenRepository.delete(token);
    }

    public ResponseEntity refreshAccessToken(String refreshToken) throws JsonProcessingException {
        Token token = findByRefreshToken(refreshToken);
        if (token == null) {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }

        if (token.getRefreshTokenExpiration().before(new Date())) {
            return ResponseEntity.badRequest().body("Refresh token expired");
        }

        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(token.getOwner().getEmail());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);

        token.setAccessToken(newAccessToken);
        token.setAccessTokenExpiration(jwtUtil.extractExpiration(newAccessToken));

        saveToken(token);
        
        String accesTokenJson = mapper.writeValueAsString(newAccessToken);

        return ResponseEntity.ok().body(accesTokenJson);
    }
}
