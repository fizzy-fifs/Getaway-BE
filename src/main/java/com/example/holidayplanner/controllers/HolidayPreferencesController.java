package com.example.holidayplanner.controllers;

import com.example.holidayplanner.models.HolidayPreferencesCreationRequest;
import com.example.holidayplanner.services.HolidayPreferencesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1.0/holidaypreferences")
@Tag(name = "Holiday Preferences")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class HolidayPreferencesController {

    private final HolidayPreferencesService holidayPreferencesService;

    public HolidayPreferencesController(HolidayPreferencesService holidayPreferencesService) {
        this.holidayPreferencesService = holidayPreferencesService;
    }

    @PostMapping(path = "/setnewholidaypreferences/{holidayId}")
    public ResponseEntity create(@PathVariable("holidayId") String holidayId, @RequestBody HolidayPreferencesCreationRequest holidayPreferences) throws JsonProcessingException {
        return holidayPreferencesService.create(holidayId, holidayPreferences.getBudget(), holidayPreferences.getAvailableDates());
    }
}
