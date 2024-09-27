package com.example.holidayplanner.holiday;

import com.example.holidayplanner.availableDates.AvailableDates;
import com.example.holidayplanner.budget.Budget;
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
