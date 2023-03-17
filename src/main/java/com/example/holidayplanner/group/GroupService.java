package com.example.holidayplanner.group;

import com.example.holidayplanner.interfaces.ServiceInterface;
import com.example.holidayplanner.user.User;
import com.example.holidayplanner.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService implements ServiceInterface<Group> {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    @Autowired
    private final ObjectMapper mapper;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository, ObjectMapper mapper) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<Object> create(Group group) throws JsonProcessingException {
         //Add group to each member's profile
//         List<User> groupMembers = group.getGroupMembers();//.toArray(new String[0]);

        List<String> groupMembersUsernames = null;

        for (User member : group.getGroupMembers()){
            groupMembersUsernames.add(member.getUserName());
        }
        var groupMembers = userRepository.findByUserNameIn(groupMembersUsernames) ;

        if (groupMembers.size() != group.getGroupMembers().size()) {
             return ResponseEntity.badRequest().body("One of the username is invalid");
        }

         //Insert group in DB
        Group newGroup = groupRepository.insert(group);

        for (User member : groupMembers){
             member.addGroup(newGroup.getId());
        }

        userRepository.saveAll(groupMembers);

        //Convert group object to json
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        String groupJson = mapper.writeValueAsString(newGroup);

        return ResponseEntity.ok(groupJson);
    }

    @Override
    public List<Group> getAll() { return groupRepository.findAll(); }

    @Override
    public String update(String entityId, Group newEntityInfo) {
        return null;
    }

    @Override
    public String delete(String groupId) {

        Group group = groupRepository.findById(new ObjectId(groupId));

        groupRepository.delete(group);

        return  group.getName() + " has been deleted" ;
    }

    public ResponseEntity<Object> addGroupMember(String groupId, String userId) {

        User newGroupMember = userRepository.findById(new ObjectId(userId));

        if (newGroupMember == null) { return ResponseEntity.badRequest().body("user with id " + userId + " does not exists"); }

        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) { return ResponseEntity.badRequest().body("Group with id " + groupId + " does not exist"); }

        group.addNewMember(newGroupMember);
        newGroupMember.addGroup(group.getId());

        groupRepository.save(group);
        userRepository.save(newGroupMember);

        return ResponseEntity.ok(newGroupMember.getFirstName() + " has been successfully added to " + group.getName());
    }

    public String removeGroupMember(String groupId, String userId) {
        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) { return "group with id " + groupId + " does not exists"; }
        group.removeMember(userId);

        groupRepository.save(group);

        return "user with id: " + userId + " has been successfully removed from " + group.getName();
    }

    public ResponseEntity<Object> findMultipleById(List<String> groupIds) throws JsonProcessingException {

        if (groupIds == null || groupIds.isEmpty()) { return ResponseEntity.badRequest().body("No group id provided"); }

        List<Group> groups = (List<Group>) groupRepository.findAllById(groupIds);

        if (groups.isEmpty()) { return ResponseEntity.badRequest().body("No group found"); }

        String groupJson = mapper.writeValueAsString(groups);

        return ResponseEntity.ok(groupJson);
    }
}
