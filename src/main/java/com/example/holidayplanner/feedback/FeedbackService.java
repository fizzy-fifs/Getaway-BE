package com.example.holidayplanner.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {
    @Autowired
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public ResponseEntity<Object> create(Feedback feedback) {
        if (feedback == null) {
            return ResponseEntity.badRequest().body("Feedback cannot be null.");
        }

        if (feedback.getUser() == null) {
            return ResponseEntity.badRequest().body("User cannot be null.");
        }

        if (feedback.getFeedback().isEmpty()) {
            return ResponseEntity.badRequest().body("Feedback cannot be empty.");
        }

        feedbackRepository.insert(feedback);
        return ResponseEntity.ok("Feedback created successfully");
    }
}
