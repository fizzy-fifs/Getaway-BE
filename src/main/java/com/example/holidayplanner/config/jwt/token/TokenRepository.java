package com.example.holidayplanner.config.jwt.token;

import com.example.holidayplanner.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TokenRepository extends MongoRepository<Token, String> {
    Token findByOwner(User user);

    Token findByAccessToken(String accessToken);

    Token findByRefreshToken(String refreshToken);

    List<Token> findAllByOwner(User user);

    List<Token> deleteAllByOwner(User user);
}
