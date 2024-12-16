package com.example.holidayplanner.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.util.Collection;

@Data
@Document(collection = "Privileges")
public class Privilege implements Serializable {

    @MongoId(FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    @DocumentReference
    private Collection<Role> roles;

    public Privilege() {
    }

    public Privilege(String name) {
        this.name = name;
    }

    public Privilege(String id, String name, Collection<Role> roles) {
        this.id = id;
        this.name = name;
        this.roles = roles;
    }
}
