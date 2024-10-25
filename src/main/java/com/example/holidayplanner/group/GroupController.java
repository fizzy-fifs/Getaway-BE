package com.example.holidayplanner.group;

import com.example.holidayplanner.group.reportGroup.ReportGroup;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0/groups")
@Tag(name = "Group")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) { this.groupService = groupService; }

    @PostMapping(path = "/newgroup")
    @Operation(summary = "Create a new group")
    public ResponseEntity<Object> create(@RequestBody @Valid Group group, Errors errors) throws JsonProcessingException {

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(errors.getAllErrors()
                            .get(0).getDefaultMessage())
                    ;
        }

        return groupService.create(group);
    }

    @GetMapping
    @Operation(summary = "Get a list of all groups")
    public List<Group> getAll() { return groupService.getAll();}


    @DeleteMapping(path = "/{groupId}")
    public ResponseEntity<String> delete(@PathVariable("groupId") String groupId) {
        return groupService.delete(groupId);
    }

    @PutMapping(path = "/addmember/{groupId}")
    public ResponseEntity<Object> addGroupMember(@PathVariable("groupId") String groupId, @RequestBody String userId) {
        return groupService.addGroupMember(groupId, userId);
    }

    @DeleteMapping(path = "/removemember/{groupId}")
    public ResponseEntity<String> removeGroupMember(@PathVariable("groupId") String groupId, @RequestBody String userId){
        return groupService.removeGroupMember(groupId, userId);
    }


    @PostMapping(path = "/findmultiplebyid")
    @Operation(summary = "Find multiple groups by their ids")
    public ResponseEntity<Object> findMultipleById(@RequestBody List<String> groupIds) throws JsonProcessingException {
        return groupService.findMultipleById(groupIds);
    }

    @GetMapping(path = "/findbyid/{groupId}")
    @Operation(summary = "Find a group by its id")
    public ResponseEntity<Object> findById(@PathVariable("groupId") String groupId) throws JsonProcessingException {
        return groupService.findById(groupId);
    }

    @GetMapping(path = "/search/{searchTerm}/{userId}")
    @Operation(summary = "Search for a group")
    public ResponseEntity<Object> search(@PathVariable("searchTerm") String searchTerm, @PathVariable("userId") String userId) throws JsonProcessingException {
        return groupService.search(searchTerm, userId);
    }

    @PutMapping(path = "/invite/{groupId}/{inviteeId}")
    @Operation(summary = "Invite multiple users to a group")
    public ResponseEntity<Object> inviteUsers(@PathVariable("groupId") String groupId, @PathVariable("inviteeId") String inviteeId, @RequestBody List<String> userIds) throws JsonProcessingException {
        return groupService.inviteUsers(groupId, inviteeId, userIds);
    }

    @PutMapping(path = "/acceptinvite/{groupInviteId}/{userId}")
    @Operation(summary = "Accept an invitation to a group")
    public ResponseEntity<Object> acceptInvitation(@PathVariable("groupInviteId") String groupInviteId, @PathVariable String userId) throws JsonProcessingException {
        return groupService.acceptInvitation(groupInviteId, userId);
    }

    @DeleteMapping(path = "/declineinvite/{groupInviteId}/{userId}")
    @Operation(summary = "Decline an invitation to a group")
    public ResponseEntity<Object> declineInvitation(@PathVariable("groupInviteId") String groupInviteId, @PathVariable String userId) throws JsonProcessingException {
        return groupService.declineInvitation(groupInviteId, userId);
    }

    @PostMapping(path = "/report")
    @Operation(summary = "Report a group")
    public ResponseEntity<Object> reportGroup(@RequestBody ReportGroup reportGroup) throws JsonProcessingException {
        return groupService.reportGroup(reportGroup);
    }

}
