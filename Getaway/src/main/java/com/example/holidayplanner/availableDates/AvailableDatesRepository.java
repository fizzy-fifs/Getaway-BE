package com.example.holidayplanner.availableDates;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AvailableDatesRepository extends MongoRepository<AvailableDates, String> {
}
