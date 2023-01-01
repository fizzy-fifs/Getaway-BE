package com.example.holidayplanner.user;

import com.example.holidayplanner.group.Group;
import com.example.holidayplanner.holiday.Holiday;
import com.example.holidayplanner.user.role.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name can only contain letters")
    @JsonProperty
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
    private String email;

    @NotBlank(message = "User name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "User name can only contain letters, numbers and underscores")
    @JsonProperty
    private String userName;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must have a minimum of 8 characters")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Password must contain at least one number, one uppercase character and one lowercase character")
    @JsonProperty
    private String password;

    @JsonProperty
    private String image;

    @JsonProperty
    @DocumentReference
    private List<Group> groups = new ArrayList<Group>();

    @JsonProperty
    @DocumentReference
    private List<User> friendRequests = new ArrayList<User>();

    @JsonProperty
    @DocumentReference
    private List<User> friends = new ArrayList<User>();

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

    public User(String firstName, String lastName, String userName, LocalDate dob, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.dob = dob;
        this.email = email;
        this.password = password;
    }

    public void addGroup(Group group) { groups.add(group); }

    public void addFriendRequest(User friendRequest) { friendRequests.add(friendRequest); }

    public void addFriend(User friend) { friends.add(friend); }
}
