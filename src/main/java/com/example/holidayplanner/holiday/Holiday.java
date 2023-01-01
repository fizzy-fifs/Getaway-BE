package com.example.holidayplanner.holiday;

import com.example.holidayplanner.availableDates.AvailableDates;
import com.example.holidayplanner.budget.Budget;
import com.example.holidayplanner.group.Group;
import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

import static java.lang.Math.round;

@Data
@Document(collection = "Holidays")
public class Holiday {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private Group group;

    @JsonProperty
    @DocumentReference
    private ArrayList<User> holidayMakers;

    @JsonProperty
    @DocumentReference
    private ArrayList<Budget> budget;

    @JsonProperty
    @DocumentReference
    private ArrayList<AvailableDates> availableDates;

    //  Constructors
    public Holiday() {
    }

    public Holiday(String name) {
        this.name = name;
    }

    public Holiday(String name, Group group) {
        this.name = name;
        this.group = group;
    }

    public Holiday(String name, Group group, ArrayList<User> holidayMakers) {
        this.name = name;
        this.group = group;
        this.holidayMakers = holidayMakers;
    }

    public Holiday(String name, Group group, ArrayList<User> holidayMakers, ArrayList<Budget> budget) {
        this.name = name;
        this.group = group;
        this.holidayMakers = holidayMakers;
        this.budget = budget;
    }

    public Holiday(String name, Group group, ArrayList<User> holidayMakers, ArrayList<Budget> budget, ArrayList<AvailableDates> availableDates) {
        this.name = name;
        this.group = group;
        this.holidayMakers = holidayMakers;
        this.budget = budget;
        this.availableDates = availableDates;
    }

    //    Methods
    public String getBudget() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(this.budget);
        } catch (JsonProcessingException e) {
            e.getMessage();
        }
        return null;
    }

    public String getAvailableDates() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(this.availableDates);
        } catch (JsonProcessingException e) {
            e.getMessage();
        }
        return null;
    }

    public void addHolidayMaker(User newHolidayMaker) {
        this.holidayMakers.add(newHolidayMaker);
    }

    public void removeHolidayMaker(User user) {
        this.holidayMakers.removeIf(
                holidayMaker -> holidayMaker.getId().equals(user.getId()));
    }

    public String[] aggregateHolidayBudgets() {

        double[] medianBudget = new double[this.budget.size()];
        for (int i = 0; i < this.budget.size(); i++) {
            var median = calculateMedian(this.budget.get(i).getBudgetUpperLimit(),
                    this.budget.get(i).getBudgetLowerLimit());
            medianBudget[i] = median;
        }

        var findMean = new Mean();
        var average = findMean.evaluate(medianBudget);

        var findSd = new StandardDeviation();
        var sd = findSd.evaluate(medianBudget);

        DecimalFormat df = new DecimalFormat("0.00");
        return new String[]{df.format(average - sd), df.format(average), df.format(average + sd)};
    }

    public String[] aggregateDates() {

        double[] startDatesArray = new double[this.availableDates.size()];
        double[] endDatesArray = new double[this.availableDates.size()];

        for (int i = 0; i < this.availableDates.size(); i++) {
            var startDateInMilli = ((double) this.availableDates.get(i).getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            var endDateInMilli = ((double) this.availableDates.get(i).getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

            startDatesArray[i] = startDateInMilli;
            endDatesArray[i] = endDateInMilli;
        }
        var findMean = new Mean();
        var averageStartDate = (long) findMean.evaluate(startDatesArray);
        var averageEndDate = (long) findMean.evaluate(endDatesArray);

        var findSd = new StandardDeviation();
        var sdOfStartDates = (long) findSd.evaluate(startDatesArray);
        var sdOfEndDates = (long) findSd.evaluate(endDatesArray);

        LocalDate suggestedStartDate1 = Instant.ofEpochMilli(averageStartDate - sdOfStartDates)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedStartDate2 = Instant.ofEpochMilli(averageStartDate)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedStartDate3 = Instant.ofEpochMilli(averageStartDate + sdOfStartDates)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedEndDate1 = Instant.ofEpochMilli(averageEndDate - sdOfEndDates)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedEndDate2 = Instant.ofEpochMilli(averageEndDate)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedEndDate3 = Instant.ofEpochMilli(averageEndDate + sdOfEndDates)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        return new String[]{
                suggestedStartDate1 + "-" + suggestedEndDate1,
                suggestedStartDate2 + "-" + suggestedEndDate2,
                suggestedStartDate3 + "-" + suggestedEndDate3
        };
    }

    private double calculateMedian(double upperLimit, double lowerLimit) {

        var range = (upperLimit - lowerLimit) + 1;
        var medianIndex = Math.floor(range / 2);

        if (range % 2 != 0) {
            return lowerLimit + medianIndex;
        } else {
            return ((lowerLimit + medianIndex) + (lowerLimit + medianIndex) - 1) / 2;
        }
    }
}
