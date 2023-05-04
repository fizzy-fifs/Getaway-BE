package com.example.holidayplanner.holiday;

import com.example.holidayplanner.availableDates.AvailableDates;
import com.example.holidayplanner.budget.Budget;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class HolidayCreationRequest {

//    @NotEmpty
    private Holiday holiday;

//    @NotEmpty
    private Budget budget;

//    @NotEmpty
    private AvailableDates availableDates;

    public HolidayCreationRequest(Holiday holiday, Budget budget, AvailableDates availableDates) {
        this.holiday = holiday;
        this.budget = budget;
        this.availableDates = availableDates;
    }
}
