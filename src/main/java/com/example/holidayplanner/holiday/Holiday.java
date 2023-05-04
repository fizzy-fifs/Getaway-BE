package com.example.holidayplanner.holiday;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Holidays")
public class Holiday {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String groupId;

    @JsonProperty
    private List<String> holidayMakersIds = new ArrayList<>();

    @JsonProperty
    private List<String> invitedHolidayMakersIds = new ArrayList<>();

    @JsonProperty
    private List<String> budgetIds = new ArrayList<>();

    @JsonProperty
    private List<String> availableDatesIds = new ArrayList<>();

    public Holiday(String name) {
        this.name = name;
    }

    public Holiday(String name, String groupId) {
        this.name = name;
        this.groupId = groupId;
    }

    public Holiday(String name, String groupId, List<String> holidayMakersIds) {
        this.name = name;
        this.groupId = groupId;
        this.holidayMakersIds = holidayMakersIds;
    }

    public Holiday(String name, String groupId, List<String> holidayMakersIds, List<String> budgetIds) {
        this.name = name;
        this.groupId = groupId;
        this.holidayMakersIds = holidayMakersIds;
        this.budgetIds = budgetIds;
    }

    public Holiday(String name, String groupId, List<String> holidayMakersIds, List<String> budgetIds, List<String> availableDatesIds) {
        this.name = name;
        this.groupId = groupId;
        this.holidayMakersIds = holidayMakersIds;
        this.budgetIds = budgetIds;
        this.availableDatesIds = availableDatesIds;
    }

    public void addHolidayMaker(String newHolidayMakerId) {
        this.holidayMakersIds.add(newHolidayMakerId);
    }

    public void removeHolidayMaker(String userId) {
        this.holidayMakersIds.remove(userId);
    }
}
