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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    private final GroupInviteRepository groupInviteRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository, ObjectMapper mapper, MongoTemplate mongoTemplate, GroupInviteRepository groupInviteRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.mongoTemplate = mongoTemplate;
        this.groupInviteRepository = groupInviteRepository;
    }

    @Override
    public ResponseEntity<Object> create(Group group) throws JsonProcessingException {

        List<String> userIds = new ArrayList<>(group.getInvitedGroupMembersIds());
        User groupCreator = group.getGroupMembers().get(0);

        userIds.add(groupCreator.getId());

        List<User> users = (List<User>) userRepository.findAllById(userIds);

        if (users.size() != userIds.size()) {
            return ResponseEntity.badRequest().body("One of the users added does not exist");
        }

        Group newGroup = groupRepository.insert(group);

        GroupInvite newGroupInvite = new GroupInvite(newGroup, groupCreator);

        GroupInvite savedGroupInvite = groupInviteRepository.insert(newGroupInvite);

        for (User invitedMember : users) {
            if (!invitedMember.getId().equals(groupCreator.getId())) {
                invitedMember.addGroupInvite(savedGroupInvite.getId());
            } else {
                invitedMember.addGroup(newGroup.getId());
            }
        }

        userRepository.saveAll(users);

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

    public ResponseEntity<Object> inviteUsers(String groupId, String inviterId, List<String> invitedUsersIds) {
        if (groupId == null || groupId.isEmpty()) {
            return ResponseEntity.badRequest().body("No group id provided");
        }
        if (invitedUsersIds == null || invitedUsersIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No user id provided");
        }
        if (inviterId == null || inviterId.isEmpty()) {
            return ResponseEntity.badRequest().body("Please include the id of the invitee");
        }

        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) {
            return ResponseEntity.badRequest().body("No group found");
        }

        List<String> userIdsToCheck = new ArrayList<>();

        userIdsToCheck.add(inviterId);
        userIdsToCheck.addAll(invitedUsersIds);

        List<User> users = (List<User>) userRepository.findAllById(userIdsToCheck);

        if (users.size() != userIdsToCheck.size()) {
            return ResponseEntity.badRequest().body("One or more of the userIds cannot be found");
        }

        User inviter = users.stream().filter(user -> user.getId().equals(inviterId)).findFirst().get();
        users.remove(inviter);

        GroupInvite newGroupInvite = new GroupInvite(group, inviter);
        GroupInvite savedGroupInvite = groupInviteRepository.save(newGroupInvite);

        for (User user : users) {
            user.getGroupInviteIds().add(savedGroupInvite.getId());
            group.getInvitedGroupMembersIds().add(user.getId());
        }

        groupRepository.save(group);
        userRepository.saveAll(users);

        return ResponseEntity.ok("Invitation sent");
    }

    public ResponseEntity<Object> acceptInvitation(String groupInviteId, String userId) {
        if (groupInviteId == null || groupInviteId.isEmpty()) {
            return ResponseEntity.badRequest().body("No group invite id provided");
        }

        GroupInvite groupInvite = groupInviteRepository.findById(new ObjectId(groupInviteId));

        if (groupInvite == null) {
            return ResponseEntity.badRequest().body("Group invite not found");
        }

        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Group group = groupInvite.getGroup();

        user.addGroup(group.getId());

        group.addNewMember(user);

        group.removeInvitedMember(user.getId());

        user.deleteGroupInvite(groupInvite.getId());

        userRepository.save(user);

        groupRepository.save(group);

        return ResponseEntity.ok("Invitation accepted");
    }

    public ResponseEntity<Object> declineInvitation(String groupInviteId, String userId) {
        if (groupInviteId == null || groupInviteId.isEmpty()) {
            return ResponseEntity.badRequest().body("No group invite provided");
        }

        GroupInvite groupInvite = groupInviteRepository.findById(new ObjectId(groupInviteId));

        if (groupInvite == null) {
            return ResponseEntity.badRequest().body("Group invite not found");
        }

        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("User with id " + userId + " does not exist");
        }

        Group group = groupInvite.getGroup();

        if (group == null) {
            return ResponseEntity.badRequest().body("Group not found");
        }

        user.deleteGroupInvite(groupInvite.getId());

        group.removeInvitedMember(user.getId());

        userRepository.save(user);

        groupRepository.save(group);

        return ResponseEntity.ok("Invitation declined");
    }
}
