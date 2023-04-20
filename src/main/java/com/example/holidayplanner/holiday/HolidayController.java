package com.example.holidayplanner.holiday;

import com.example.holidayplanner.interfaces.ControllerInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0/holidays")
@Api(tags = "Holiday")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class HolidayController implements ControllerInterface<Holiday> {

    private final HolidayService holidayService;

    @Autowired
    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @Override
    @PostMapping(path = "/newholiday")
    public ResponseEntity create(Holiday holiday, Errors errors) throws JsonProcessingException {
        return holidayService.create(holiday);
    }

    @GetMapping(path = "/addholidaymaker/holiday={holidayId}&user={userId}")
    public ResponseEntity sendHolidayInvite(@PathVariable("holidayId") String holidayId, @PathVariable("userId") String userId) {
        return holidayService.addHolidayMaker(holidayId, userId);
    }

    @GetMapping(path = "/removeholidaymaker/holiday={holidayId}&user={userId}")
    public String removeHolidayMaker(@PathVariable("holidayId") String holidayId, @PathVariable("userId") String userId) {
        return holidayService.removeHolidayMaker(holidayId, userId);
    }

    @GetMapping(path = "/getbudgetaggregates/{holidayId}")
    public String[] getBudgetAggregates(@PathVariable("holidayId") String holidayId) {
        return holidayService.aggregateHolidayBudgets(holidayId);
    }

    @GetMapping(path = "/getdateaggregates/{holidayId}")
    public String[] getDateAggregates(@PathVariable("holidayId") String holidayId) {
        return holidayService.aggregateDates(holidayId);
    }

    @Override
    public List<Holiday> getAll() {
        return null;
    }

    @Override
    public String update(String id, Holiday newInfo) {
        return null;
    }

    @Override
    public String delete(String id) {
        return null;
    }
}
