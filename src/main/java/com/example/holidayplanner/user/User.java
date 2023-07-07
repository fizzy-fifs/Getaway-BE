package com.example.holidayplanner.user;

import com.example.holidayplanner.user.role.Role;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Document(collection="Users")
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

    @JsonFormat( pattern = "dd/MM/yyyy" )
    @DateTimeFormat( pattern = "dd/MM/yyyy" )
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonProperty
    private LocalDate dob;

    @NotBlank(message = "Email cannot be blank")
    @Email(message="Please provide a valid email address")
    @Pattern(regexp=".+@.+\\..+", message="Please provide a valid email address")
    @JsonProperty
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "User name cannot be blank")
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
    private List<GroupInvite> groupInvites = new ArrayList<>();

    @JsonProperty
    private List<String> holidayIds = new ArrayList<>();

    @JsonProperty
    private List<HolidayInvite> holidayInvites = new ArrayList<>();

    @JsonProperty
    private List<String> friendRequests = new ArrayList<>();

    @JsonProperty
    private List<String> friendRequestsSent = new ArrayList<>();

    @JsonProperty
    @Nullable
    private String deviceToken;


    @JsonProperty
    private List<String> friends = new ArrayList<>();

    @JsonProperty
    private Collection<Role> roles;

    @JsonProperty
    private boolean enabled;

    public User() {}

    public User(String id, String firstName, String lastName, String userName, LocalDate dob, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.dob = dob;
        this.email = email;
        this.password = password;
    }

    public User(String firstName, String lastName, String userName, LocalDate dob, String email, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.dob = dob;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public void addGroupInvite(GroupInvite groupInvite) { groupInvites.add(groupInvite); }

    public void addGroup(String groupId) { groupIds.add(groupId); }

    public void addFriendRequest(String friendId) { friendRequests.add(friendId); }

    public void addToFriendRequestsSent(String id) { friendRequestsSent.add(id); }

    public void removeFromFreindRequestsSent(String id) { friendRequestsSent.remove(id); }

    public void deleteFriendRequest(String rejectedFriendId) {  friendRequests.remove(rejectedFriendId); }

    public void addFriend(String friendId) { friends.add(friendId); }

    public void addHoliday(String holidayId) { holidayIds.add(holidayId); }

    public void deleteHoliday(String holidayId) { holidayIds.remove(holidayId); }

    public void addHolidayInvite(HolidayInvite holidayInvite) { holidayInvites.add(holidayInvite); }

    public void deleteHolidayInvite(String holidayId) { holidayInvites.remove(holidayId); }


}
