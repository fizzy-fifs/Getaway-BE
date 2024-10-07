package com.example.holidayplanner.holiday;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0/holidays")
@Tag(name = "Holiday")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class HolidayController {

    private final HolidayService holidayService;

    @Autowired
    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @PostMapping(path = "/newholiday")
    public ResponseEntity create(@RequestBody @Valid HolidayCreationRequest holidayCreationRequest) throws JsonProcessingException {
        return holidayService.create(holidayCreationRequest.getHoliday(), holidayCreationRequest.getBudget(), holidayCreationRequest.getAvailableDates());
    }

    @GetMapping(path = "/removeholidaymaker/holiday={holidayId}&user={userId}")
    public String removeHolidayMaker(@PathVariable("holidayId") String holidayId, @PathVariable("userId") String userId) {
        return holidayService.removeHolidayMaker(holidayId, userId);
    }

    @GetMapping(path = "/getaggregates/{holidayId}")
    public ResponseEntity getDateAndBudgetAggregates(@PathVariable("holidayId") String holidayId) throws JsonProcessingException {
        return holidayService.getDateAndBudgetAggregates(holidayId);
    }

    @PostMapping(path = "/findmultiplebyid")
    @Operation(summary = "Find multiple holidays by their ids")
    public ResponseEntity<Object> findMultipleById(@RequestBody List<String> holidayIds) throws JsonProcessingException {
        return holidayService.findMultipleById(holidayIds);
    }

    @GetMapping(path = "/findbyid/{holidayId}")
    @Operation(summary = "Find a single holiday by its id")
    public ResponseEntity<Object> findById(@PathVariable("holidayId") String holidayId) throws JsonProcessingException {
        return holidayService.findById(holidayId);
    }

    @GetMapping(path= "/acceptinvite/{holidayInviteId}/{userId}")
    @Operation(summary = "Accept a holiday invite")
    public ResponseEntity<Object> acceptInvite(@PathVariable("holidayInviteId") String holidayInviteId, @PathVariable("userId") String userId) {
        return holidayService.acceptInvite(holidayInviteId, userId);
    }

    @GetMapping(path= "/declineinvite/{holidayInviteId}/{userId}")
    @Operation(summary = "Decline a holiday invite")
    public ResponseEntity declineInvite(@PathVariable("holidayInviteId") String holidayInviteId, @PathVariable("userId") String userId) {
        return holidayService.declineInvite(holidayInviteId, userId);
    }
}
