package com.example.holidayplanner.models;

import com.example.holidayplanner.models.AvailableDates;
import com.example.holidayplanner.models.Budget;
import lombok.Data;

@Data
public class HolidayPreferencesCreationRequest {
    private Budget budget;

    private AvailableDates availableDates;

    public HolidayPreferencesCreationRequest(Budget budget, AvailableDates availableDates) {
        this.budget = budget;
        this.availableDates = availableDates;
    }
}
