package com.example.holidayplanner.group;

import com.example.holidayplanner.groupInvite.GroupInviteRepository;
import com.example.holidayplanner.interfaces.ServiceInterface;
import com.example.holidayplanner.groupInvite.GroupInvite;
import com.example.holidayplanner.user.User;
import com.example.holidayplanner.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService implements ServiceInterface<Group> {
    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ObjectMapper mapper;

    @Autowired
    private final MongoTemplate mongoTemplate;

    @Autowired
    private GroupInviteRepository groupInviteRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository, ObjectMapper mapper, MongoTemplate mongoTemplate) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ResponseEntity<Object> create(Group group) throws JsonProcessingException {
        // Combining invited members and the group creator into one list and checking if all the users exist
        List<User> invitedMembers = group.getInvitedGroupMembers();
        User groupCreator = group.getGroupMembers().get(0);

        List<String> userIdsToCheck = invitedMembers.stream()
                .map(User::getId)
                .collect(Collectors.toList());
        userIdsToCheck.add(groupCreator.getId());

        List<User> confirmedUsers = (List<User>) userRepository.findAllById(userIdsToCheck);

        if (confirmedUsers.size() != userIdsToCheck.size()) {
            return ResponseEntity.badRequest().body("One of the users added does not exist");
        }

        GroupInvite newGroupInvite = new GroupInvite(group, groupCreator);

        GroupInvite savedGroupInvite = groupInviteRepository.insert(newGroupInvite);

        for (User invitedMember : confirmedUsers) {
            if (!invitedMember.getId().equals(groupCreator.getId())) {
                invitedMember.addGroupInvite(savedGroupInvite);
            }
        }
        Group newGroup = groupRepository.insert(group);

        groupCreator.addGroup(newGroup.getId());

        userRepository.saveAll(confirmedUsers);

        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        String groupJson = mapper.writeValueAsString(newGroup);

        return ResponseEntity.ok(groupJson);
    }

    @Override
    public List<Group> getAll() {
        return groupRepository.findAll();
    }

    @Override
    public String update(String entityId, Group newEntityInfo) {
        return null;
    }

    @Override
    public String delete(String groupId) {

        Group group = groupRepository.findById(new ObjectId(groupId));

        groupRepository.delete(group);

        return group.getName() + " has been deleted";
    }

    public ResponseEntity<Object> addGroupMember(String groupId, String userId) {

        User newGroupMember = userRepository.findById(new ObjectId(userId));

        if (newGroupMember == null) {
            return ResponseEntity.badRequest().body("user with id " + userId + " does not exists");
        }

        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) {
            return ResponseEntity.badRequest().body("Group with id " + groupId + " does not exist");
        }

        group.addNewMember(newGroupMember);
        newGroupMember.addGroup(group.getId());

        groupRepository.save(group);
        userRepository.save(newGroupMember);

        return ResponseEntity.ok(newGroupMember.getFirstName() + " has been successfully added to " + group.getName());
    }

    public String removeGroupMember(String groupId, String userId) {
        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) {
            return "group with id " + groupId + " does not exists";
        }
        group.removeMember(userId);

        groupRepository.save(group);

        return "user with id: " + userId + " has been successfully removed from " + group.getName();
    }

    public ResponseEntity<Object> findMultipleById(List<String> groupIds) throws JsonProcessingException {

        if (groupIds == null || groupIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No group id provided");
        }

        List<Group> groups = (List<Group>) groupRepository.findAllById(groupIds);

        if (groups.isEmpty()) {
            return ResponseEntity.badRequest().body("No group found");
        }

        String groupJson = mapper.writeValueAsString(groups);

        return ResponseEntity.ok(groupJson);
    }

    public ResponseEntity<Object> findById(String groupId) throws JsonProcessingException {
        if (groupId == null || groupId.isEmpty()) {
            return ResponseEntity.badRequest().body("No group id provided");
        }

        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) {
            return ResponseEntity.badRequest().body("No group found");
        }

        String groupJson = mapper.writeValueAsString(group);

        return ResponseEntity.ok(groupJson);
    }

    public ResponseEntity<List<Group>> search(String searchTerm) {
        String sanitizedSearchTerm = searchTerm.trim().toLowerCase();

        Query searchQuery = new Query();
        Criteria criteria = new Criteria().orOperator(Criteria.where("name").regex(sanitizedSearchTerm, "i"), Criteria.where("description").regex(sanitizedSearchTerm, "i"));

        searchQuery.addCriteria(criteria);
        int pageSize = 10;
        int pageNumber = 0;
        searchQuery.with(PageRequest.of(pageNumber, pageSize));

        List<Group> groups = mongoTemplate.find(searchQuery, Group.class);

        return ResponseEntity.ok(groups);
    }


    public ResponseEntity<Object> inviteUsers(String groupId, String inviteeId, List<String> invitedUsersIds) {
        if (groupId == null || groupId.isEmpty()) {
            return ResponseEntity.badRequest().body("No group id provided");
        }
        if (invitedUsersIds == null || invitedUsersIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No user id provided");
        }
        if (inviteeId == null || inviteeId.isEmpty()) {
            return ResponseEntity.badRequest().body("Please include the id of the invitee");
        }

        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) {
            return ResponseEntity.badRequest().body("No group found");
        }

        invitedUsersIds.add(inviteeId);
        List<User> users = (List<User>) userRepository.findAllById(invitedUsersIds);

        if (users.size() != invitedUsersIds.size() + 1) {
            return ResponseEntity.badRequest().body("One or more of the userIds cannot be found");
        }

        User inviter = users.stream().filter(user -> user.getId().equals(inviteeId)).findFirst().get();
        users.remove(inviter);

        GroupInvite newGroupInvite = new GroupInvite(group, inviter);
        GroupInvite savedGroupInvite = groupInviteRepository.save(newGroupInvite);

        for (User user : users) {
            user.getGroupInvites().add(savedGroupInvite);
        }

        userRepository.saveAll(users);

        return ResponseEntity.ok("Invitation sent");
    }

    public ResponseEntity<Object> acceptInvitation(String groupInviteId, String userId) {
        if (groupInviteId == null || groupInviteId.isEmpty()) {
            return ResponseEntity.badRequest().body("No group invite provided");
        }

        GroupInvite groupInvite = groupInviteRepository.findById(new ObjectId(groupInviteId));

        if (groupInvite == null) {
            return ResponseEntity.badRequest().body("No group invite found");
        }

        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        user.addGroup(groupInvite.group.getId());

        user.getGroupInvites().remove(groupInvite);

        groupInvite.group.addNewMember(user);

        userRepository.save(user);

        groupRepository.save(groupInvite.group);

        return ResponseEntity.ok("Invitation accepted");
    }

    public ResponseEntity<Object> declineInvitation(String groupInviteId, String userId) {
        if (groupInviteId == null || groupInviteId.isEmpty()) {
            return ResponseEntity.badRequest().body("No group invite provided");
        }

        GroupInvite groupInvite = groupInviteRepository.findById(new ObjectId(groupInviteId));

        if (groupInvite == null) {
            return ResponseEntity.badRequest().body("No group invite found");
        }

        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("User with id " + userId + " does not exist");
        }

        user.getGroupInvites().remove(groupInvite);

        groupInvite.group.getInvitedGroupMembers().removeIf(invitedGroupMember -> invitedGroupMember.getId().equals(user.getId()));

        userRepository.save(user);

        groupRepository.save(groupInvite.group);

        return ResponseEntity.ok("Invitation declined");
    }
}
