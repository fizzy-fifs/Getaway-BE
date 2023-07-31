package com.example.holidayplanner.availableDates;

import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@Document(collection = "Available Dates")
@NoArgsConstructor
@AllArgsConstructor
public class AvailableDates {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    @NotEmpty
    private String userId;

    @JsonFormat( pattern = "dd/MM/yyyy" )
    @DateTimeFormat( pattern = "dd/MM/yyyy" )
    @JsonProperty
    @NotEmpty
    private LocalDate startDate;

    @JsonFormat( pattern = "dd/MM/yyyy" )
    @DateTimeFormat( pattern = "dd/MM/yyyy" )
    @JsonProperty
    @NotEmpty
    private LocalDate endDate;

    @JsonProperty
    @NotEmpty
    private int flexibility;

    @JsonProperty
    private int nights;


    public AvailableDates(String userId, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AvailableDates(String userId, LocalDate startDate, LocalDate endDate, int flexibility) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.flexibility = flexibility;
    }

    public AvailableDates(String userId, LocalDate startDate, LocalDate endDate, int flexibility, int nights) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.flexibility = flexibility;
        this.nights = nights;
    }

    public long getNights() {
        return this.nights = (int) ChronoUnit.DAYS.between(this.startDate, this.endDate);
    }
}
