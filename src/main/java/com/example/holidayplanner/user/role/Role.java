package com.example.holidayplanner.user.role;

import com.example.holidayplanner.user.User;
import com.example.holidayplanner.user.privilege.Privilege;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Collection;

@Data
@Document(collection = "Roles")
public class Role {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    @DocumentReference
    private Collection<User> users;

    @JsonProperty
    @DocumentReference
    private Collection<Privilege> privileges;


    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public Role(String id, String name, Collection<User> users, Collection<Privilege> privileges) {
        this.id = id;
        this.name = name;
        this.users = users;
        this.privileges = privileges;
    }
}
