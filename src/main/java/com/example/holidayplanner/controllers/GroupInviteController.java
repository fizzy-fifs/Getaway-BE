package com.example.holidayplanner.controllers;

import com.example.holidayplanner.services.GroupInviteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/groupinvite")
@Tag(name = "Group Invite")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class GroupInviteController {
    private final GroupInviteService groupInviteService;

    public GroupInviteController(GroupInviteService groupInviteService) {
        this.groupInviteService = groupInviteService;
    }

    @GetMapping(path= "/{groupInviteId}")
    @Operation(summary = "Find group invite by id")
    public ResponseEntity<String> findById(@PathVariable("groupInviteId") String groupInviteId) throws JsonProcessingException {
        return groupInviteService.findById(groupInviteId);
    }

    @PostMapping(path = "/findmultiplebyid")
    @Operation(summary = "Find multiple group invites by their ids")
    public ResponseEntity<String> findMultipleById(@RequestBody List<String> groupInviteIds) throws JsonProcessingException {
        return groupInviteService.findMultipleById(groupInviteIds);
    }
}
