package com.example.holidayplanner.user;

import com.example.holidayplanner.group.Group;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupInvite {

    @JsonProperty
    public Group groupId;

    @JsonProperty
    public User inviteeId;
}
