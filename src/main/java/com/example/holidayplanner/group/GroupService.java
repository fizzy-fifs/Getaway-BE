package com.example.holidayplanner.group;

import com.example.holidayplanner.interfaces.ServiceInterface;
import com.example.holidayplanner.user.User;
import com.example.holidayplanner.user.UserRepository;
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
    public ResponseEntity create(Group group) {
         groupRepository.insert(group);
         return ResponseEntity.ok("Group has been successfully created");
    }

    @Override
    public List<Group> getAll() { return groupRepository.findAll(); }

    @Override
    public String update(String entityId, Group newEntityInfo) {
        return null;
    }

    @Override
    public String delete(String groupId) {

        Group group = groupRepository.findById(groupId).get();

        groupRepository.delete(group);

        return  group.getName() + " has been deleted" ;
    }

    public String addGroupMember(String groupId, String userId) {

        User newGroupMember = userRepository.findById(userId).get();

        if (newGroupMember == null) { return "user with id " + userId + " does not exists"; }

        Group group = groupRepository.findById(groupId).get();

        group.addNewMember(newGroupMember);

        groupRepository.save(group);

        return newGroupMember.getFirstName() + " has been successfully added to " + group.getName();
    }

    public String removeGroupMember(String groupId, String userId) {
        Group group = groupRepository.findById(groupId).get();

        if (group == null) { return "group with id " + groupId + " does not exists"; }
        group.removeMember(userId);

        groupRepository.save(group);

        return "user with id: " + userId + " has been successfully removed from " + group.getName();
    }
}
