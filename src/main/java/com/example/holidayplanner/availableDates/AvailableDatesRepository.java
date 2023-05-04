package com.example.holidayplanner.availableDates;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AvailableDatesRepository extends MongoRepository<AvailableDates, String> {
}
