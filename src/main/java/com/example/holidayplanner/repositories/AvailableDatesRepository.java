package com.example.holidayplanner.repositories;

import com.example.holidayplanner.models.AvailableDates;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AvailableDatesRepository extends MongoRepository<AvailableDates, String> {
}
