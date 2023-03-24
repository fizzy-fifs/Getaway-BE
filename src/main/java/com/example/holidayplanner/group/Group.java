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
    private List<String> groupMembers = new ArrayList<>();



    @JsonProperty
    private String description;

    @JsonProperty
    private List<String> holidayIds = new ArrayList<>();

    public Group(String name) {
        this.name = name;
    }

    public Group(String name, List<String> groupMembers) { this.name = name; this.groupMembers = groupMembers; }
    
    public Group(String name, List<User> groupMembers, String description) {
        this.name = name;
        this.groupMembers = groupMembers;
        this.description = description;
    }
    
    public void addNewMember(String newGroupMemberId) { this.groupMembers.add(newGroupMemberId); }

    public void removeMember(String memberId) { this.groupMembers.remove(memberId); }

    public void addHoliday(String holidayId) { holidayIds.add(holidayId); }
}
