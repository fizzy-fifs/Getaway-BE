package com.example.holidayplanner.config.jwt.token;

import com.example.holidayplanner.user.User;
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

    public Token findByUserId(User user) {
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
}
