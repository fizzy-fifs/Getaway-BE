package com.example.holidayplanner.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HolidayInvite {

    @JsonProperty
    public String holidayId;

    @JsonProperty
    public String inviteeId;
}
