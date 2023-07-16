package com.example.holidayplanner.groupInvite;

import com.example.holidayplanner.group.Group;
import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotNull;

@Data
@Document(collection="GroupInvites")
public class GroupInvite {
    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    @NotNull
    public Group group;

    @JsonProperty
    @NotNull
    public User invitee;

    public GroupInvite() {
    }

    public GroupInvite(Group group, User invitee) {
        this.group = group;
        this.invitee = invitee;
    }
}
