package com.example.holidayplanner.models;

import com.example.holidayplanner.models.AvailableDates;
import com.example.holidayplanner.models.Budget;
import com.example.holidayplanner.models.Holiday;
import lombok.Data;

@Data
public class HolidayCreationRequest {

    private Holiday holiday;

    private Budget budget;

    private AvailableDates availableDates;

    public HolidayCreationRequest(Holiday holiday, Budget budget, AvailableDates availableDates) {
        this.holiday = holiday;
        this.budget = budget;
        this.availableDates = availableDates;
    }
}
