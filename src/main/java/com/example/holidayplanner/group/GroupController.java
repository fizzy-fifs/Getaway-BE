package com.example.holidayplanner.group;

import com.example.holidayplanner.interfaces.ControllerInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0/groups")
public class GroupController implements ControllerInterface<Group> {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) { this.groupService = groupService; }

    @Override
    @PostMapping(path = "/newgroup")
    public ResponseEntity create(Group group, Errors errors) throws JsonProcessingException {
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
    public String addGroupMember(@PathVariable("groupId") String groupId, @RequestBody String userId) {
        return groupService.addGroupMember(groupId, userId);
    }

    @PostMapping(path = "/removemember/{groupId}")
    public String removeGroupMember(@PathVariable("groupId") String groupId, @RequestBody String userId){
        return groupService.removeGroupMember(groupId, userId);
    }
}
