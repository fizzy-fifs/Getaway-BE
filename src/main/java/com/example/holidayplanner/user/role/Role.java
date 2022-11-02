package com.example.holidayplanner.user.role;

import com.example.holidayplanner.user.User;
import com.example.holidayplanner.user.privilege.Privilege;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Collection;

@Data
@Document
public class Role {

    @MongoId(value = FieldType.OBJECT_ID)
    private String id;

    private String name;

    private Collection<User> users;

    private Collection<Privilege> privileges;


    public Role(String name) {
        this.name = name;
    }
}
