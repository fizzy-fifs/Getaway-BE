package com.example.holidayplanner.repositories;

import com.example.holidayplanner.models.user.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByName(String name);
}
