package com.example.holidayplanner.group;

import com.example.holidayplanner.group.reportGroup.ReportGroup;
import com.example.holidayplanner.group.reportGroup.ReportGroupRepository;
import com.example.holidayplanner.groupInvite.GroupInviteRepository;
import com.example.holidayplanner.helpers.CacheHelper;
import com.example.holidayplanner.helpers.Helper;
import com.example.holidayplanner.groupInvite.GroupInvite;
import com.example.holidayplanner.user.User;
import com.example.holidayplanner.user.UserRepository;
import com.example.holidayplanner.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {
    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ObjectMapper mapper;

    @Autowired
    private final MongoTemplate mongoTemplate;

    @Autowired
    private final GroupInviteRepository groupInviteRepository;

    @Autowired
    private final ReportGroupRepository reportGroupRepository;

    private final CacheHelper<Group> groupCacheHelper;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserService userService, UserRepository userRepository, ObjectMapper mapper, MongoTemplate mongoTemplate, GroupInviteRepository groupInviteRepository, ReportGroupRepository reportGroupRepository, CacheManager cacheManager) {
        this.groupRepository = groupRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.mongoTemplate = mongoTemplate;
        this.groupInviteRepository = groupInviteRepository;
        this.reportGroupRepository = reportGroupRepository;
        this.groupCacheHelper = new CacheHelper<>(cacheManager, "groups", Group.class);
    }

    public ResponseEntity<Object> create(Group group) throws JsonProcessingException {

        List<String> userIds = new ArrayList<>(group.getInvitedGroupMembersIds());
        User groupCreator = group.getGroupMembers().get(0);

        userIds.add(groupCreator.getId());

        List<User> users = userService.findMultipleUsersByIdInCacheOrDatabase(userIds);

        if (users.size() != userIds.size()) {
            return ResponseEntity.badRequest().body("One of the users added does not exist");
        }

        for (User user : users) {
            if (user.getBlockedUserIds().contains(groupCreator.getId())) {
                group.getGroupMembers().remove(user);
                group.getInvitedGroupMembersIds().remove(user.getId());
                users.remove(user);
            }
        }

        group.setName(Helper.toSentenceCase(group.getName()));
        group.setDescription(Helper.toSentenceCase(group.getDescription()));

        Group newGroup = groupRepository.insert(group);

        GroupInvite newGroupInvite = new GroupInvite(newGroup, groupCreator);

        GroupInvite savedGroupInvite = groupInviteRepository.insert(newGroupInvite);

        for (User invitedMember : users) {
            if (!invitedMember.getId().equals(groupCreator.getId())) {
                invitedMember.addGroupInviteId(savedGroupInvite.getId());
            } else {
                invitedMember.addGroup(newGroup.getId());
            }
        }

        userService.updateMultipleUsersInCacheAndDatabase(users);

        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        String groupJson = mapper.writeValueAsString(newGroup);

        return ResponseEntity.ok(groupJson);
    }

    public ResponseEntity<String> delete(String groupId) {

        Group group = findSingleGroupByIdInCacheOrDatabase(groupId);

        if (group == null) {
            return ResponseEntity.badRequest().body("Group with id: " + groupId + "does not exist");
        }

        groupRepository.delete(group);
        groupCacheHelper.removeEntryFromCache(groupId);

        return ResponseEntity.ok(group.getName() + " has been deleted");
    }

    public ResponseEntity<Object> addGroupMember(String groupId, String userId) {

        User newGroupMember = userService.findSingleUserByIdInCacheOrDatabase(userId);

        if (newGroupMember == null) {
            return ResponseEntity.badRequest().body("user with id " + userId + " does not exists");
        }

        Group group = findSingleGroupByIdInCacheOrDatabase(groupId);

        if (group == null) {
            return ResponseEntity.badRequest().body("Group with id " + groupId + " does not exist");
        }

        group.addNewMember(newGroupMember);
        newGroupMember.addGroup(group.getId());

        updateSingleGroupInCacheAndDatabase(group);
        userService.updateSingleUserInCacheAndDatabase(newGroupMember);

        return ResponseEntity.ok(newGroupMember.getFirstName() + " has been successfully added to " + group.getName());
    }

    public ResponseEntity<String> removeGroupMember(String groupId, String userId) {
        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) {
            return ResponseEntity.badRequest().body("group with id " + groupId + " does not exists");
        }
        group.removeMember(userId);

        updateSingleGroupInCacheAndDatabase(group);

        return ResponseEntity.ok("user with id: " + userId + " has been successfully removed from " + group.getName());
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

        Group group = findSingleGroupByIdInCacheOrDatabase(groupId);

        if (group == null) {
            return ResponseEntity.badRequest().body("No group found");
        }

        String groupJson = mapper.writeValueAsString(group);

        return ResponseEntity.ok(groupJson);
    }

    public ResponseEntity<Object> search(String searchTerm, String userId) {
        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("User with id " + userId + " does not exist");
        }

        String sanitizedSearchTerm = searchTerm.trim().toLowerCase();

        List<Object> recentGroupSearchHistory = user.getRecentGroupSearchHistory();

        recentGroupSearchHistory.remove(sanitizedSearchTerm);

        recentGroupSearchHistory.add(0, sanitizedSearchTerm);

        if (recentGroupSearchHistory.size() > 10) {
            recentGroupSearchHistory.remove(recentGroupSearchHistory.size() - 1);
        }

        user.setRecentGroupSearchHistory(recentGroupSearchHistory);

        Query searchQuery = new Query();
        Criteria criteria = new Criteria().orOperator(Criteria.where("name").regex(sanitizedSearchTerm, "i"), Criteria.where("description").regex(sanitizedSearchTerm, "i"));

        searchQuery.addCriteria(criteria);
        int pageSize = 10;
        int pageNumber = 0;
        searchQuery.with(PageRequest.of(pageNumber, pageSize));

        List<Group> groups = mongoTemplate.find(searchQuery, Group.class);

        userRepository.save(user);

        return ResponseEntity.ok(groups);
    }

    public ResponseEntity<Object> inviteUsers(String groupId, String invitingUserId, List<String> invitedUsersIds) {
        if (groupId == null || groupId.isEmpty()) {
            return ResponseEntity.badRequest().body("No group id provided");
        }
        if (invitedUsersIds == null || invitedUsersIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No user id provided");
        }
        if (invitingUserId == null || invitingUserId.isEmpty()) {
            return ResponseEntity.badRequest().body("Please include the id of the inviting user");
        }

        invitedUsersIds.remove(invitingUserId);

        Group group = findSingleGroupByIdInCacheOrDatabase(groupId);

        if (group == null) {
            return ResponseEntity.badRequest().body("No group found");
        }

        List<String> userIdsToCheck = new ArrayList<>();

        userIdsToCheck.add(invitingUserId);
        userIdsToCheck.addAll(invitedUsersIds);

        List<User> users = (List<User>) userRepository.findAllById(userIdsToCheck);

        if (users.size() != userIdsToCheck.size()) {
            return ResponseEntity.badRequest().body("One or more of the userIds cannot be found");
        }

        User invitingUser = users.stream().filter(user -> user.getId().equals(invitingUserId)).findFirst().get();
        users.remove(invitingUser);
        users.removeIf(user -> group.getGroupMembers().contains(user) || group.getInvitedGroupMembersIds().contains(user.getId()) || user.getGroupIds().contains(group.getId()));
        users.removeIf(user -> user.getBlockedUserIds().contains(invitingUser.getId()));

        GroupInvite newGroupInvite = new GroupInvite(group, invitingUser);
        GroupInvite savedGroupInvite = groupInviteRepository.save(newGroupInvite);

        for (User user : users) {
            user.getGroupInviteIds().add(savedGroupInvite.getId());
            group.getInvitedGroupMembersIds().add(user.getId());
        }

        updateSingleGroupInCacheAndDatabase(group);
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

        updateSingleGroupInCacheAndDatabase(group);

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

        updateSingleGroupInCacheAndDatabase(group);

        return ResponseEntity.ok("Invitation declined");
    }

    public ResponseEntity<Object> reportGroup(ReportGroup reportGroup) {
        if (reportGroup == null) {
            return ResponseEntity.badRequest().body("No report provided");
        }

        Group group = reportGroup.getGroup();

        if (group == null) {
            return ResponseEntity.badRequest().body("No group provided");
        }

        User user = reportGroup.getUser();

        if (user == null) {
            return ResponseEntity.badRequest().body("No user provided");
        }

        String reason = reportGroup.getReason();

        if (reason == null || reason.isEmpty()) {
            return ResponseEntity.badRequest().body("No reason provided");
        }

        Group groupToReport = findSingleGroupByIdInCacheOrDatabase(group.getId());

        if (groupToReport == null) {
            return ResponseEntity.badRequest().body("Group not found");
        }

        User userReporting = userRepository.findById(new ObjectId(user.getId()));

        if (userReporting == null) {
            return ResponseEntity.badRequest().body("Reporting user not found");
        }

        ReportGroup newReportGroup = new ReportGroup(groupToReport, userReporting, reason, LocalDateTime.now());

        ReportGroup savedReportGroup = reportGroupRepository.insert(newReportGroup);

        return ResponseEntity.ok(savedReportGroup);
    }

    @Cacheable(value = "groups", key = "#id", unless = "#result == null")
    public Group findSingleGroupByIdInCacheOrDatabase(String groupId) {
        return groupRepository.findById(new ObjectId(groupId));
    }

    public Group updateSingleGroupInCacheAndDatabase(Group group) {
        Group cachedGroup = groupCacheHelper.getCachedEntry(group.getId());
        if (cachedGroup != null) { groupCacheHelper.cacheEntry(group, group.getId()); }

        return groupRepository.save(group);
    }
}
