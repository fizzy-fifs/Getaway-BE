package com.example.holidayplanner.group;

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
    private List<String> groupMemberUsernames;

    @JsonProperty
    private List<String> holidayIds = new ArrayList<>();


    public Group(String name, List<String> groupMemberUsernames) { this.name = name; this.groupMemberUsernames = groupMemberUsernames; }

    public void addNewMember(String newGroupMemberUsername) { this.groupMemberUsernames.add(newGroupMemberUsername); }

    public void removeMember(String username) { this.groupMemberUsernames.removeIf(memberUsername -> memberUsername.equals(username)); }

    public void addHoliday(String holidayId) { holidayIds.add(holidayId); }
}
