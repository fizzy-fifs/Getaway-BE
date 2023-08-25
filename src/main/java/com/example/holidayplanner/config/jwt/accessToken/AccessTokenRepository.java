package com.example.holidayplanner.config.jwt.accessToken;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccessTokenRepository extends MongoRepository<AccessToken, String> {
}
