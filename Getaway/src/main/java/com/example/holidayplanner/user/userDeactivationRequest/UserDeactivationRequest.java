package com.example.holidayplanner.user.userDeactivationRequest;

import com.example.holidayplanner.config.cascadeSaveMongoEventListener.CascadeSave;
import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Document(collection="User Deactivation Requests")
public class UserDeactivationRequest {
    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    @DBRef
    @CascadeSave
    private User user;

    @JsonProperty
    private LocalDateTime dateRequested;

    public UserDeactivationRequest() {
    }

    public UserDeactivationRequest(User user, LocalDateTime dateRequested) {
        this.user = user;
        this.dateRequested = dateRequested;
    }
}
