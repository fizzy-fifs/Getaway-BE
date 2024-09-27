package com.example.holidayplanner.holidayInvite;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HolidayInviteRepository extends MongoRepository<HolidayInvite, String> {
    HolidayInvite findById(ObjectId objectId);
}
