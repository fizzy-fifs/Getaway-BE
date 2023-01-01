package com.example.holidayplanner.availableDates;

import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@Document(collection = "Available Dates")
public class AvailableDates {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    @DocumentReference
    private User user;

    @JsonProperty
    private LocalDate startDate;

    @JsonProperty
    private LocalDate endDate;

    @JsonProperty
    private int flexibility;

    @JsonProperty
    private int nights;

    public AvailableDates(User user, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AvailableDates(User user, LocalDate startDate, LocalDate endDate, int flexibility) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.flexibility = flexibility;
    }

    public AvailableDates(User user, LocalDate startDate, LocalDate endDate, int flexibility, int nights) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.flexibility = flexibility;
        this.nights = nights;
    }

    public long getNights() {
        return this.nights = (int) ChronoUnit.DAYS.between(this.startDate, this.endDate);
    }
}
