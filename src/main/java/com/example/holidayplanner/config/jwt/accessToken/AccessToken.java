package com.example.holidayplanner.config.jwt.accessToken;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "Access Tokens")
@Data
public class AccessToken {

    @MongoId(value = FieldType.OBJECT_ID)
    private String id;

    private String token;

    public AccessToken(String token) {
        this.token = token;
    }
}