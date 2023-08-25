package com.example.holidayplanner.config.jwt.token;

import org.springframework.stereotype.Service;
@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token saveToken(Token token) {
        return tokenRepository.save(token);
    }

    public Token findByUserId(String userId) {
        return tokenRepository.findByUserId(userId);
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
}
