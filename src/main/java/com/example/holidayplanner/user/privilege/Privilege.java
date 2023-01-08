package com.example.holidayplanner.user.privilege;

import com.example.holidayplanner.user.role.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Collection;

@Data
@Document(collection = "Privileges")
public class Privilege {

    @MongoId(FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    @DocumentReference
    private Collection<Role> roles;

    public Privilege(String name) {
        this.name = name;
    }
}
