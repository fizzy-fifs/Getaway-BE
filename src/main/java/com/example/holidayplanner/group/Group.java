package com.example.holidayplanner.group;

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
@Document(collection="Groups")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Group {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    @DBRef
    private List<User> groupMembers = new ArrayList<>();

    @JsonProperty
    @DBRef
    private List<User> invitedGroupMembers = new ArrayList<>();

    @JsonProperty
    private String description;

    @JsonProperty
    private String image;

    @JsonProperty
    @DBRef
    private List<Holiday> holidays = new ArrayList<>();


    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(String name, List<User> groupMembers) { this.name = name; this.groupMembers = groupMembers; }

    public Group(String name, List<User> groupMembers, List<User> invitedGroupMembers) {
        this.name = name;
        this.groupMembers = groupMembers;
        this.invitedGroupMembers = invitedGroupMembers;
    }

    public Group(String name, List<User> groupMembers, String description) {
        this.name = name;
        this.groupMembers = groupMembers;
        this.description = description;
    }

    public Group(String name, List<User> groupMembers, String description, String image) {
        this.name = name;
        this.groupMembers = groupMembers;
        this.description = description;
        this.image = image;
    }

    public void addNewMember(User newGroupMember) { this.groupMembers.add(newGroupMember); }

    public void removeMember(String memberId) { this.groupMembers.removeIf(member -> Objects.equals(member.getId(), memberId)); }

    public void addHoliday(Holiday holiday) { holidays.add(holiday); }
}
