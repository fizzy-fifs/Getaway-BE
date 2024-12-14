package com.example.holidayplanner.scheduler;

import com.example.holidayplanner.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Scheduler {
    @Autowired
    private final MongoTemplate mongoTemplate;

    public Scheduler(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

//    @Scheduled(fixedRate = 1000 * 60)
    public void updateUserPropertiesIfNotPresent() {
        mongoTemplate.updateMulti(new Query(), new Update()
                .setOnInsert("isVerified", false), User.class);
    }
}
