package com.example.holidayplanner.groupInvite;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/groupinvite")
@Api(tags = "Group Invite")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class GroupInviteController {
    private GroupInviteService groupInviteService;

    @GetMapping(path= "/{groupInviteId}")
    @ApiOperation(value = "Find group invite by id")
    public ResponseEntity findGroupInviteById(@PathVariable("groupInviteId") String groupInviteId) {
        return groupInviteService.findGroupInviteById(groupInviteId);
    }
}
