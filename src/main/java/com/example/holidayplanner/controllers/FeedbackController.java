package com.example.holidayplanner.controllers;

import com.example.holidayplanner.models.Feedback;
import com.example.holidayplanner.services.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1.0/feedbacks")
@Tag(name = "Feedback")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class FeedbackController {

    @Autowired
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping(path = "/newfeedback")
    public ResponseEntity<Object> createFeedback(@RequestBody @Valid Feedback feedback, Errors errors) {

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(errors.getAllErrors()
                            .get(0).getDefaultMessage())
                    ;
        }

        return feedbackService.create(feedback);
    }
}
