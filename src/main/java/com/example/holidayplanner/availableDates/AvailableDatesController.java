package com.example.holidayplanner.availableDates;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/availabledates")
@Tag(name = "Available Dates")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class AvailableDatesController {

    @Autowired
    private final AvailableDatesService availableDatesService;

    public AvailableDatesController(AvailableDatesService availableDatesService) {
        this.availableDatesService = availableDatesService;
    }


    @PostMapping(path = "/findmultiplebyid")
    @Operation(summary = "Find multiple available dates by id")
    public ResponseEntity findMultipleById(@RequestBody List<String> availableDatesIds) throws JsonProcessingException {
        return availableDatesService.findMultipleById(availableDatesIds);
    }
}
