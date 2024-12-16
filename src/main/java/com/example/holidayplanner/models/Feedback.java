package com.example.holidayplanner.models;

import com.example.holidayplanner.models.user.User;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotNull;

@Data
@Document(collection = "Feedbacks")
public class Feedback {
    @MongoId(value = FieldType.OBJECT_ID)
    private String id;

    @NotNull(message = "User cannot be null")
    private User user;

    @NotNull(message = "Feedback cannot be null")
    private String feedback;

    public Feedback(User user) {
        this.user = user;
    }

    public Feedback(User user, String feedback) {
        this.user = user;
        this.feedback = feedback;
    }
}
