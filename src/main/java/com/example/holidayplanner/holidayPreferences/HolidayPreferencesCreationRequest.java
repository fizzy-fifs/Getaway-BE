package com.example.holidayplanner.holidayPreferences;

import com.example.holidayplanner.availableDates.AvailableDates;
import com.example.holidayplanner.budget.Budget;
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
