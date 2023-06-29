package com.example.holidayplanner.user;

import com.example.holidayplanner.group.Group;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupInvite {

    @JsonProperty
    public Group group;

    @JsonProperty
    public User invitee;

    public GroupInvite() {
    }

    public GroupInvite(Group group, User invitee) {
        this.group = group;
        this.invitee = invitee;
    }
}
