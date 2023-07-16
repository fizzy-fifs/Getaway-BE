package com.example.holidayplanner.groupInvite;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupInviteRepository extends MongoRepository<GroupInvite, String> {
    GroupInvite findById(ObjectId groupInviteId);
}
