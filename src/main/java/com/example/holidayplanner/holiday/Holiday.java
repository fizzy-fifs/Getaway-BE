package com.example.holidayplanner.holiday;

import com.example.holidayplanner.availableDates.AvailableDates;
import com.example.holidayplanner.budget.Budget;
import com.example.holidayplanner.config.cascadeSaveMongoEventListener.CascadeSave;
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
    private String groupId;

    @JsonProperty
    @DBRef
    @CascadeSave
    private List<User> holidayMakers = new ArrayList<>();

    @JsonProperty
    @DBRef
    @CascadeSave
    private List<User> invitedHolidayMakers = new ArrayList<>();

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

    public Holiday(String name, String groupId, List<User> holidayMakers) {
        this.name = name;
        this.groupId = groupId;
        this.holidayMakers = holidayMakers;
    }

    public Holiday(String name, String groupId, List<User> holidayMakers, List<String> budgetIds) {
        this.name = name;
        this.groupId = groupId;
        this.holidayMakers = holidayMakers;
        this.budgetIds = budgetIds;
    }

    public Holiday(String name, String groupId, List<User> holidayMakers, List<String> budgetIds, List<String> availableDates) {
        this.name = name;
        this.groupId = groupId;
        this.holidayMakers = holidayMakers;
        this.budgetIds = budgetIds;
        this.availableDatesIds = availableDatesIds;
    }

    public void addHolidayMaker(User newHolidayMakerId) {
        this.holidayMakers.add(newHolidayMakerId);
    }

    public void removeHolidayMaker(String userId) {
        this.holidayMakers.removeIf(holidayMakerId -> holidayMakerId.equals(userId));
    }

    public void removeInvitedHolidayMaker(String userId) {
        this.invitedHolidayMakers.removeIf(invitedHolidayMakerId -> invitedHolidayMakerId.equals(userId));
    }

    public void addBudget(String newBudgetId) {
        this.budgetIds.add(newBudgetId);
    }

    public void addAvailableDates(String availableDatesIds) {
        this.availableDatesIds.add(availableDatesIds);
    }
}
