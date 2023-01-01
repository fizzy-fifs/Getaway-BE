package com.example.holidayplanner.group;

import com.example.holidayplanner.holiday.Holiday;
import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;

@Data
@Document(collection="Groups")
public class Group {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    @DocumentReference
    private ArrayList<User> groupMembers;

    @JsonProperty
    @DocumentReference
    private ArrayList<Holiday> holidays;


    public Group(String name, ArrayList<User> groupMembers) { this.name = name; this.groupMembers = groupMembers; }

    public void addNewMember(User newGroupMember) { this.groupMembers.add(newGroupMember); }

    public void removeMember(String id) { this.groupMembers.removeIf(member -> member.getId().equals(id)); }

    public void addHoliday(Holiday holiday) { holidays.add(holiday); }
}
