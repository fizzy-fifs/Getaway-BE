package com.example.holidayplanner.holidayInvite;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/holidayinvite")
@Api(tags = "Holiday Invite")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class HolidayInviteController {

    @Autowired
    private final HolidayInviteService holidayInviteService;

    public HolidayInviteController(HolidayInviteService holidayInviteService) {
        this.holidayInviteService = holidayInviteService;
    }

    @PostMapping("/findmultiplebyid")
    @ApiOperation(value = "Find multiple holiday invites by their ids")
    public ResponseEntity findMultipleById(@RequestBody List<String> holidayInviteIds) throws JsonProcessingException {
        return holidayInviteService.findMultipleById(holidayInviteIds);
    }
}
