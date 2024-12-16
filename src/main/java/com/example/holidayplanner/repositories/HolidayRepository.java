package com.example.holidayplanner.repositories;

import com.example.holidayplanner.models.Holiday;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HolidayRepository extends MongoRepository<Holiday, String> {
    Holiday findById(ObjectId Id);
}
