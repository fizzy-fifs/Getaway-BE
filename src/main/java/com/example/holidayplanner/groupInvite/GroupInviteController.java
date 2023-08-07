package com.example.holidayplanner.groupInvite;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/groupinvite")
@Api(tags = "Group Invite")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class GroupInviteController {
    private final GroupInviteService groupInviteService;

    public GroupInviteController(GroupInviteService groupInviteService) {
        this.groupInviteService = groupInviteService;
    }

    @GetMapping(path= "/{groupInviteId}")
    @ApiOperation(value = "Find group invite by id")
    public ResponseEntity findById(@PathVariable("groupInviteId") String groupInviteId) throws JsonProcessingException {
        return groupInviteService.findById(groupInviteId);
    }

    @PostMapping(path = "/findmultiplebyid")
    @ApiOperation(value = "Find multiple group invites by their ids")
    public ResponseEntity findMultipleById(@RequestBody List<String> groupInviteIds) throws JsonProcessingException {
        return groupInviteService.findMultipleById(groupInviteIds);
    }
}
