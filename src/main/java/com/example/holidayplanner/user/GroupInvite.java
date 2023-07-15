package com.example.holidayplanner.user;

import com.example.holidayplanner.group.Group;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupInvite {

    @JsonProperty
    public String groupId;

    @JsonProperty
    public String inviteeId;

    public GroupInvite() {
    }

    public GroupInvite(String groupId, String inviteeId) {
        this.groupId = groupId;
        this.inviteeId = inviteeId;
    }
}
