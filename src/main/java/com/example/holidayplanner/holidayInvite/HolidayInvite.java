package com.example.holidayplanner.holidayInvite;

import com.example.holidayplanner.holiday.Holiday;
import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection="Holiday Invites")
public class HolidayInvite {
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
