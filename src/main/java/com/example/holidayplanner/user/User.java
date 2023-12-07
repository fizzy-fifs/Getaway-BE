package com.example.holidayplanner.user;

;
import com.example.holidayplanner.user.role.Role;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.lang.Nullable;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Document(collection = "Users")
public class User {
    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @NotBlank(message = "First name cannot be blank")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name can only contain letters")
    @JsonProperty
    @Indexed
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name can only contain letters")
    @JsonProperty
    @Indexed
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    @Pattern(regexp = ".+@.+\\..+", message = "Please provide a valid email address")
    @JsonProperty
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "User name cannot be blank")
    @NotNull(message = "Please provide a user name")
    @NotEmpty(message = "Please add a user name")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "User name can only contain letters, numbers and underscores")
    @JsonProperty
    @Indexed(unique = true)
    private String userName;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must have a minimum of 8 characters")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Password must contain at least one number, one uppercase character and one lowercase character")
    @JsonProperty
    private String password;

    @Size(min = 7, max = 11, message = "Please enter a valid phone number")
    @JsonProperty
    @Nullable
    private String phoneNumber;

    @JsonProperty
    @Nullable
    private String image;

    @JsonProperty
    private List<String> groupIds = new ArrayList<>();

    @JsonProperty
    private List<String> groupInviteIds = new ArrayList<>();

    @JsonProperty
    private List<String> holidayIds = new ArrayList<>();

    @JsonProperty
    private List<String> holidayInviteIds = new ArrayList<>();

    @JsonProperty
    private List<String> friendRequests = new ArrayList<>();

    @JsonProperty
    private List<String> friendRequestsSent = new ArrayList<>();

    @JsonProperty
    private List<String> blockedUserIds = new ArrayList<>();

    @JsonProperty
    private List<String> blockedByUserIds = new ArrayList<>();

    @JsonProperty
    @Nullable
    private String deviceToken;

    @JsonProperty
    private boolean isActive = true;

    @JsonProperty
    private LocalDate dateJoined;

    @JsonProperty
    private LocalDateTime lastLogin;

    @JsonProperty
    private List<String> friends = new ArrayList<>();

    @JsonProperty
    private List<Object> recentUserSearchHistory = new ArrayList<>();

    @JsonProperty
    private List<Object> recentGroupSearchHistory = new ArrayList<>();

    @JsonProperty
    private Collection<Role> roles;

    @JsonProperty
    private boolean enabled;

    public User() {
    }

    public User(String firstName, String lastName, String userName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public void addGroupInviteId(String groupInviteId) {
        groupInviteIds.add(groupInviteId);
    }

    public void addHolidayInviteId(String holidayInviteId) {
        holidayInviteIds.add(holidayInviteId);
    }

    public void addGroup(String groupId) {
        groupIds.add(groupId);
    }

    public void addFriendRequest(String friendId) {
        friendRequests.add(friendId);
    }

    public void addToFriendRequestsSent(String id) {
        friendRequestsSent.add(id);
    }

    public void removeFromFreindRequestsSent(String id) {
        friendRequestsSent.remove(id);
    }

    public void deleteFriendRequest(String rejectedFriendId) {
        friendRequests.remove(rejectedFriendId);
    }

    public void addFriend(String friendId) {
        friends.add(friendId);
    }

    public void removeFriend(String friendId) {
        friends.remove(friendId);
    }

    public void addHoliday(String holidayId) {
        holidayIds.add(holidayId);
    }

    public void addHolidayInvite(String holidayInviteId) {
        holidayInviteIds.add(holidayInviteId);
    }

    public void deleteHolidayInvite(String holidayInviteId) {
        holidayInviteIds.remove(holidayInviteId);
    }

    public void deleteGroupInvite(String groupInviteId) {
        groupInviteIds.remove(groupInviteId);
    }

    public void addBlockedUser(String userId) {
        blockedUserIds.add(userId);
    }

    public void addBlockedByUser(String userId) {
        blockedByUserIds.add(userId);
    }

    public void removeBlockedUser(String userId) {
        blockedUserIds.remove(userId);
    }

    public void removeBlockedByUser(String userId) {
        blockedByUserIds.remove(userId);
    }
}
