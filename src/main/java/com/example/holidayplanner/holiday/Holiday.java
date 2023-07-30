package com.example.holidayplanner.holiday;

import com.example.holidayplanner.availableDates.AvailableDates;
import com.example.holidayplanner.budget.Budget;
import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Holidays")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Holiday {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    @Nullable
    private String image;

    @JsonProperty
    @DBRef
    private String groupId;

    @JsonProperty
    @DBRef
    private List<String> holidayMakersIds = new ArrayList<>();

    @JsonProperty
    @DBRef
    private List<String> invitedHolidayMakersIds = new ArrayList<>();

    @JsonProperty
    @DBRef
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

    public Holiday(String name, String groupId, List<String> holidayMakersIds, List<String> budgetIds, List<String> availableDates) {
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
        this.holidayMakersIds.removeIf(holidayMakerId -> holidayMakerId.equals(userId));
    }

    public void removeInvitedHolidayMaker(String userId) {
        this.invitedHolidayMakersIds.removeIf(invitedHolidayMakerId -> invitedHolidayMakerId.equals(userId));
    }

    public void addBudget(String newBudgetId) {
        this.budgetIds.add(newBudgetId);
    }

    public void addAvailableDates(String availableDatesIds) {
        this.availableDatesIds.add(availableDatesIds);
    }
}
