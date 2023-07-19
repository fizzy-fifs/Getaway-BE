package com.example.holidayplanner.holidayInvite;

import com.example.holidayplanner.holiday.Holiday;
import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection="HolidayInvites")
public class HolidayInvite {
    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private Holiday holiday;

    @JsonProperty
    private User invitee;

    public HolidayInvite() {
    }

    public HolidayInvite(Holiday holiday, User invitee) {
        this.holiday = holiday;
        this.invitee = invitee;
    }
}
