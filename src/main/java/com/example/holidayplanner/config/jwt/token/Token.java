package com.example.holidayplanner.config.jwt.token;

import com.example.holidayplanner.config.cascadeSaveMongoEventListener.CascadeSave;
import com.example.holidayplanner.models.user.User;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Document(collection = "Tokens")
@Data
public class Token {

    @MongoId(value = FieldType.OBJECT_ID)
    private String id;

    @DBRef
    @CascadeSave
    private User owner;

    private String accessToken;

    private String refreshToken;

    private Date accessTokenExpiration;

    private Date refreshTokenExpiration;
}
