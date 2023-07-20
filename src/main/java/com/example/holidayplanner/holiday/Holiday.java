package com.example.holidayplanner.holiday;

import com.example.holidayplanner.availableDates.AvailableDates;
import com.example.holidayplanner.budget.Budget;
import com.example.holidayplanner.group.Group;
import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private Group group;

    @JsonProperty
    @DBRef
    private List<User> holidayMakers = new ArrayList<>();

    @JsonProperty
    @DBRef
    private List<User> invitedHolidayMakers = new ArrayList<>();

    @JsonProperty
    @DBRef
    private List<Budget> budgets = new ArrayList<>();

    @JsonProperty
    private List<AvailableDates> availableDates = new ArrayList<>();

    public Holiday(String name) {
        this.name = name;
    }

    public Holiday(String name, Group group) {
        this.name = name;
        this.group = group;
    }

    public Holiday(String name, Group group, List<User> holidayMakers) {
        this.name = name;
        this.group = group;
        this.holidayMakers = holidayMakers;
    }

    public Holiday(String name, Group group, List<User> holidayMakers, List<Budget> budgets) {
        this.name = name;
        this.group = group;
        this.holidayMakers = holidayMakers;
        this.budgets = budgets;
    }

    public Holiday(String name, Group group, List<User> holidayMakers, List<Budget> budgets, List<AvailableDates> availableDates) {
        this.name = name;
        this.group = group;
        this.holidayMakers = holidayMakers;
        this.budgets = budgets;
        this.availableDates = availableDates;
    }

    public void addHolidayMaker(User newHolidayMaker) {
        this.holidayMakers.add(newHolidayMaker);
    }

    public void removeHolidayMaker(User user) {
        this.holidayMakers.removeIf(holidayMaker -> holidayMaker.getId().equals(user.getId()));
    }

    public void removeInvitedHolidayMaker(User user) {
        this.invitedHolidayMakers.removeIf(invitedHolidayMaker -> invitedHolidayMaker.getId().equals(user));
    }

    public void addBudget(Budget newBudget) {
        this.budgets.add(newBudget);
    }

    public void addAvailableDates(AvailableDates availableDates) {
        this.availableDates.add(availableDates);
    }
}
