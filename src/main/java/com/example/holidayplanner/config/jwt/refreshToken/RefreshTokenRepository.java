package com.example.holidayplanner.config.jwt.refreshToken;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

}
