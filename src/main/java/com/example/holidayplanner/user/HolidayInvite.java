package com.example.holidayplanner.user;

import com.example.holidayplanner.holiday.Holiday;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HolidayInvite {

    @JsonProperty
    public Holiday holidayId;

    @JsonProperty
    public User inviteeId;

    public HolidayInvite(Holiday holidayId, User inviteeId) {
        this.holidayId = holidayId;
        this.inviteeId = inviteeId;
    }
}
