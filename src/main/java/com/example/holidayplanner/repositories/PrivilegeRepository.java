package com.example.holidayplanner.repositories;

import com.example.holidayplanner.models.user.Privilege;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrivilegeRepository extends MongoRepository<Privilege, String> {
    Privilege findByName(String name);
}
