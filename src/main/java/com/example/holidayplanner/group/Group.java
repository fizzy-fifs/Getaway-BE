package com.example.holidayplanner.group;

import com.example.holidayplanner.config.cascadeSaveMongoEventListener.CascadeSave;
import com.example.holidayplanner.holiday.Holiday;
import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Document(collection = "Groups")
public class Group {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    @DBRef
    @CascadeSave
    private List<String> groupMemberIds = new ArrayList<>();

    @JsonProperty
    private List<String> invitedGroupMemberIds = new ArrayList<>();

    @JsonProperty
    private String description;

    @JsonProperty
    private String image;

    @JsonProperty
    @DBRef
    @CascadeSave
    private List<Holiday> holidays = new ArrayList<>();


    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(String name, List<String> groupMemberIds) {
        this.name = name;
        this.groupMemberIds = groupMemberIds;
    }

    public Group(String name, List<String> groupMemberIds, List<String> invitedGroupMemberIds) {
        this.name = name;
        this.groupMemberIds = groupMemberIds;
        this.invitedGroupMemberIds = invitedGroupMemberIds;
    }

    public Group(String name, List<String> groupMemberIds, String description) {
        this.name = name;
        this.groupMemberIds = groupMemberIds;
        this.description = description;
    }

    public Group(String name, List<String> groupMemberIds, String description, String image) {
        this.name = name;
        this.groupMemberIds = groupMemberIds;
        this.description = description;
        this.image = image;
    }

    public void addNewMember(String newGroupMemberId) {
        this.groupMemberIds.add(newGroupMemberId);
    }

    public void removeMember(String memberId) { this.groupMemberIds.remove(memberId); }

    public void addHoliday(Holiday holidayId) {
        holidays.add(holidayId);
    }

    public void removeInvitedMember(String invitedMemberId) {
        this.invitedGroupMemberIds.removeIf(memberId -> Objects.equals(memberId, invitedMemberId));
    }
}
