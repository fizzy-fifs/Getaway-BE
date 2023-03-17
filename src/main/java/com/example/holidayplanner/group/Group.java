package com.example.holidayplanner.group;

import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection="Groups")
public class Group {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private List<User> groupMembers;

    @JsonProperty
    private List<String> holidayIds = new ArrayList<>();


    public Group(String name, List<User> groupMembers) { this.name = name; this.groupMembers = groupMembers; }

    public void addNewMember(User newGroupMember) { this.groupMembers.add(newGroupMember); }

    public void removeMember(String username) { this.groupMembers.removeIf(member -> member.getUserName().equals(username)); }

    public void addHoliday(String holidayId) { holidayIds.add(holidayId); }
}
