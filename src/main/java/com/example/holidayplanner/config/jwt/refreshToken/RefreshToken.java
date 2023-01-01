package com.example.holidayplanner.config.jwt.refreshToken;

import com.example.holidayplanner.user.User;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;


@Document(collection = "Refresh Tokens")
@Data
public class RefreshToken {

    @MongoId(value = FieldType.OBJECT_ID)
    private String id;

    @DocumentReference
    private User owner;
}
