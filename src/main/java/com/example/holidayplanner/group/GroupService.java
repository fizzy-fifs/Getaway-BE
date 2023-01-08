package com.example.holidayplanner.group;

import com.example.holidayplanner.interfaces.ServiceInterface;
import com.example.holidayplanner.user.User;
import com.example.holidayplanner.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService implements ServiceInterface<Group> {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<Object> create(Group group) throws JsonProcessingException {
         //Add group to each member's profile
         List<String> groupMemberUsernames = group.getGroupMemberUsernames();//.toArray(new String[0]);

         var groupMembers = userRepository.findByUserNameIn(groupMemberUsernames);

         if (groupMembers.size() != groupMemberUsernames.size()) {
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

        group.addNewMember(newGroupMember.getUserName());
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
}
