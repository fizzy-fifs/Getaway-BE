package com.example.holidayplanner.user;

import com.example.holidayplanner.holiday.Holiday;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HolidayInvite {

    @JsonProperty
    public Holiday holiday;

    @JsonProperty
    public User invitee;

    public HolidayInvite() {
    }

    public HolidayInvite(Holiday holiday, User invitee) {
        this.holiday = holiday;
        this.invitee = invitee;
    }
}
