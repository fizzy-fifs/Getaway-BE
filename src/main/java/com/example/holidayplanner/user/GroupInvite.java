package com.example.holidayplanner.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupInvite {

    @JsonProperty
    public String groupId;

    @JsonProperty
    public String inviteeId;
}
