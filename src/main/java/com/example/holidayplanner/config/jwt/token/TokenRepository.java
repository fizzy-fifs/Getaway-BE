package com.example.holidayplanner.config.jwt.token;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, String> {
    Token findByUserId(String userId);

    Token findByAccessToken(String accessToken);

    Token findByRefreshToken(String refreshToken);
}
