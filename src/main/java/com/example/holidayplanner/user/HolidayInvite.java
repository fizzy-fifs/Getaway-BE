package com.example.holidayplanner.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HolidayInvite {

    @JsonProperty
    public String holidayId;

    @JsonProperty
    public String inviteeId;

    public HolidayInvite(String holidayId, String inviteeId) {
        this.holidayId = holidayId;
        this.inviteeId = inviteeId;
    }
}
