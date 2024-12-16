package com.example.holidayplanner.models;

import com.example.holidayplanner.models.user.User;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

@Data
@Document(collection="Holiday Invites")
public class HolidayInvite implements Serializable {
    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    @DBRef
    private Holiday holiday;

    @JsonProperty
    @DBRef
    private User invitee;

    public HolidayInvite() {
    }

    public HolidayInvite(Holiday holiday, User invitee) {
        this.holiday = holiday;
        this.invitee = invitee;
    }
}
