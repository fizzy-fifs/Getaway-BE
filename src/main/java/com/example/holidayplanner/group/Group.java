package com.example.holidayplanner.group;

import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Document(collection="Groups")
public class Group {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private List<String> groupMembersIds = new ArrayList<>();

    @JsonProperty
    private List<String> invitedGroupMembersIds = new ArrayList<>();

    @JsonProperty
    private String description;

    @JsonProperty
    private String image;

    @JsonProperty
    private List<String> holidayIds = new ArrayList<>();


    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(String name, List<String> groupMembersIds) { this.name = name; this.groupMembersIds = groupMembersIds; }

    public Group(String name, List<String> groupMembersIds, List<String> invitedGroupMembersIds) {
        this.name = name;
        this.groupMembersIds = groupMembersIds;
        this.invitedGroupMembersIds = invitedGroupMembersIds;
    }

    public Group(String name, List<String> groupMembersIds, String description) {
        this.name = name;
        this.groupMembersIds = groupMembersIds;
        this.description = description;
    }

    public Group(String name, List<String> groupMembersIds, String description, String image) {
        this.name = name;
        this.groupMembersIds = groupMembersIds;
        this.description = description;
        this.image = image;
    }

    public void addNewMemberId(String newGroupMember) { this.groupMembersIds.add(newGroupMember); }

    public void removeMember(String memberId) { this.groupMembersIds.remove(memberId); }

    public void addHoliday(String holidayId) { holidayIds.add(holidayId); }
}
