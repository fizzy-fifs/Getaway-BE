package com.example.holidayplanner.repositories.group;

import com.example.holidayplanner.models.group.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {
    Group findById(ObjectId Id);
}
