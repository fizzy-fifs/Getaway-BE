package com.example.holidayplanner.holiday;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0/holidays")
@Api(tags = "Holiday")
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

    @GetMapping(path = "/addholidaymaker/holiday={holidayId}&user={userId}")
    public ResponseEntity sendHolidayInvite(@PathVariable("holidayId") String holidayId, @PathVariable("userId") String userId) {
        return holidayService.addHolidayMaker(holidayId, userId);
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
    @ApiOperation(value = "Find multiple holidays by their ids")
    public ResponseEntity<Object> findMultipleById(@RequestBody List<String> holidayIds) throws JsonProcessingException {
        return holidayService.findMultipleById(holidayIds);
    }

    @GetMapping(path = "/findbyid/{holidayId}")
    @ApiOperation(value = "Find a single holiday by its id")
    public ResponseEntity<Object> findById(@PathVariable("holidayId") String holidayId) throws JsonProcessingException {
        return holidayService.findById(holidayId);
    }

    @GetMapping(path= "/acceptinvite/holiday={holidayId}&user={userId}")
    @ApiOperation(value = "Accept a holiday invite")
    public ResponseEntity<Object> acceptInvite(@PathVariable("holidayId") String holidayId, @PathVariable("userId") String userId) throws JsonProcessingException {
        return holidayService.acceptInvite(holidayId, userId);
    }

    @GetMapping(path= "/declineinvite/holiday={holidayId}&user={userId}")
    @ApiOperation(value = "Decline a holiday invite")
    public ResponseEntity<Object> declineInvite(@PathVariable("holidayId") String holidayId, @PathVariable("userId") String userId) {
        return holidayService.declineInvite(holidayId, userId);
    }
}
