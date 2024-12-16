package com.example.holidayplanner.models;

import com.example.holidayplanner.config.cascadeSaveMongoEventListener.CascadeSave;
import com.example.holidayplanner.models.group.Group;
import com.example.holidayplanner.models.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Document(collection = "Group Invites")
public class GroupInvite implements Serializable {
    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    @NotNull
    @DBRef
    @CascadeSave
    public Group group;

    @JsonProperty
    @NotNull
    @DBRef
    @CascadeSave
    public User invitee;

    public GroupInvite() {
    }

    public GroupInvite(Group group, User invitee) {
        this.group = group;
        this.invitee = invitee;
    }
}
