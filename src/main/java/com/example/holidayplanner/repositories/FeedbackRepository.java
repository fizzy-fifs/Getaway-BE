package com.example.holidayplanner.repositories;

import com.example.holidayplanner.models.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
}
