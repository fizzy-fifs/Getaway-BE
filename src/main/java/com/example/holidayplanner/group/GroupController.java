package com.example.holidayplanner.group;

import com.example.holidayplanner.interfaces.ControllerInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0/groups")
@Api(tags = "Group")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class GroupController implements ControllerInterface<Group> {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) { this.groupService = groupService; }

    @Override
    @PostMapping(path = "/newgroup")
    public ResponseEntity<Object> create(@RequestBody @Valid Group group, Errors errors) throws JsonProcessingException {
        return groupService.create(group);
    }

    @Override
    @GetMapping
    public List<Group> getAll() { return groupService.getAll();}

    @Override
    public String update(String id, Group newInfo) {
        return null;
    }

    @Override
    @DeleteMapping(path = "/{groupId}")
    public String delete(@PathVariable("groupId") String groupId) {
        return groupService.delete(groupId);
    }

    @PostMapping(path = "/addmember/{groupId}")
    public ResponseEntity<Object> addGroupMember(@PathVariable("groupId") String groupId, @RequestBody String userId) {
        return groupService.addGroupMember(groupId, userId);
    }

    @PostMapping(path = "/removemember/{groupId}")
    public String removeGroupMember(@PathVariable("groupId") String groupId, @RequestBody String userId){
        return groupService.removeGroupMember(groupId, userId);
    }


    @PostMapping(path = "/findmultiplebyid")
    @ApiOperation(value = "Find multiple groups by their ids")
    public ResponseEntity<Object> findMultipleById(@RequestBody List<String> groupIds) throws JsonProcessingException {
        return groupService.findMultipleById(groupIds);
    }

    @GetMapping(path = "/findbyid/{groupId}")
    @ApiOperation(value = "Find a group by its id")
    public ResponseEntity<Object> findById(@PathVariable("groupId") String groupId) throws JsonProcessingException {
        return groupService.findById(groupId);
    }

    @GetMapping(path = "/search/{searchTerm}")
    @ApiOperation(value = "Search for a group")
    public ResponseEntity<List<Group>> search(@PathVariable("searchTerm") String searchTerm) throws JsonProcessingException {
        return groupService.search(searchTerm);
    }

    @PostMapping(path = "/invite/{groupId}/{inviteeId}")
    @ApiOperation(value = "Invite multiple users to a group")
    public ResponseEntity<Object> inviteUsers(@PathVariable("groupId") String groupId, @PathVariable("inviteeId") String inviteeId, @RequestBody List<String> userIds) throws JsonProcessingException {
        return groupService.inviteUsers(groupId, inviteeId, userIds);
    }

    @PostMapping(path = "/acceptinvite/{groupId}/{userId}")
    @ApiOperation(value = "Accept an invitation to a group")
    public ResponseEntity<Object> acceptInvitation(@PathVariable("groupId") String groupId, @PathVariable String userId) throws JsonProcessingException {
        return groupService.acceptInvitation(groupId, userId);
    }

    @PostMapping(path = "/declineinvite/{groupId}/{userId}")
    @ApiOperation(value = "Decline an invitation to a group")
    public ResponseEntity<Object> declineInvitation(@PathVariable("groupId") String groupId, @PathVariable String userId) throws JsonProcessingException {
        return groupService.declineInvitation(groupId, userId);
    }

}
