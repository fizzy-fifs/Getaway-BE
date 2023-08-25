package com.example.holidayplanner.config.jwt.token;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "Tokens")
@Data
public class Token {

    @MongoId(value = FieldType.OBJECT_ID)
    private String id;

    private String userId;

    private String accessToken;

    private String refreshToken;
}
