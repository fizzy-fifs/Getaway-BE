package com.example.holidayplanner.group;

import com.example.holidayplanner.interfaces.ServiceInterface;
import com.example.holidayplanner.user.GroupInvite;
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
    public GroupService(GroupRepository groupRepository, UserRepository userRepository, ObjectMapper mapper, MongoTemplate mongoTemplate) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ResponseEntity<Object> create(Group group) throws JsonProcessingException {
         if (!group.getInvitedGroupMembersIds().isEmpty()) {
             System.out.println("InvitedGroupMembersIds is not empty");
             List<User> invitedGroupMembers = userRepository.findByUserNameIn(group.getInvitedGroupMembersIds());

             if (invitedGroupMembers.size() != group.getInvitedGroupMembersIds().size()) {
                 return ResponseEntity.badRequest().body("One of the usernames added is invalid");
             }

             GroupInvite newGroupInvite = new GroupInvite(group, group.getGroupMembers().get(0));

             for (User invitedMember : invitedGroupMembers){
                 invitedMember.addGroupInvite(newGroupInvite);
             }

             userRepository.saveAll(invitedGroupMembers);
         }

        Group newGroup = groupRepository.insert(group);

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

    public ResponseEntity<Object> findById(String groupId) throws JsonProcessingException {
        if (groupId == null || groupId.isEmpty()) { return ResponseEntity.badRequest().body("No group id provided"); }

        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) { return ResponseEntity.badRequest().body("No group found"); }

        String groupJson = mapper.writeValueAsString(group);

        return ResponseEntity.ok(groupJson);
    }

    public ResponseEntity<List<Group>> search(String searchTerm) {
        String sanitizedSearchTerm = searchTerm.trim().toLowerCase();

        Query searchQuery = new Query();
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("name").regex(sanitizedSearchTerm, "i"),
                Criteria.where("description").regex(sanitizedSearchTerm, "i")
        );

        searchQuery .addCriteria(criteria);
        int pageSize = 10;
        int pageNumber = 0;
        searchQuery.with(PageRequest.of(pageNumber, pageSize));

        List<Group> groups = mongoTemplate.find(searchQuery, Group.class);

        return ResponseEntity.ok(groups);
    }


    public ResponseEntity<Object> inviteUsers(String groupId, String inviteeId, List<String> inviteeIds) {
        if (groupId == null || groupId.isEmpty()) { return ResponseEntity.badRequest().body("No group id provided"); }
        if (inviteeIds == null || inviteeIds.isEmpty()) { return ResponseEntity.badRequest().body("No user id provided"); }
        if (inviteeId == null || inviteeId.isEmpty()) { return ResponseEntity.badRequest().body("Please include the id of the invitee"); }

        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) { return ResponseEntity.badRequest().body("No group found"); }

        List<User> invitees = (List<User>) userRepository.findAllById(inviteeIds);

        if (invitees.size() != inviteeIds.size()) { return ResponseEntity.badRequest().body("One or more of the userIds cannot be found"); }

        User inviter = userRepository.findById(new ObjectId(inviteeId));

        if (inviter == null) { return ResponseEntity.badRequest().body("User with id " + inviteeId + " does not exist"); }

        GroupInvite newGroupInvitation = new GroupInvite(group, inviter);

        for (User user : invitees) {
            user.getGroupInvites().add(newGroupInvitation);
        }

        userRepository.saveAll(invitees);

        return ResponseEntity.ok("Invitation sent");
    }

    public ResponseEntity<Object> acceptInvitation(String groupId, String userId) {
        if (groupId == null || groupId.isEmpty()) { return ResponseEntity.badRequest().body("No group id provided"); }

        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) { return ResponseEntity.badRequest().body("No group found"); }

        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) { return ResponseEntity.badRequest().body("User with id " + userId + " does not exist"); }

        user.addGroup(group.getId());

        user.getGroupInvites().remove(group.getId());

        group.addNewMember(user);

        userRepository.save(user);

        groupRepository.save(group);

        return ResponseEntity.ok("Invitation accepted");
    }
}
