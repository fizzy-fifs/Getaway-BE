package com.example.holidayplanner.user;

import com.example.holidayplanner.userLookupModel.UserLookupModel;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner commandLineRunner(UserRepository repository) {
        return args -> { repository.findAll(); };
    }

    @Bean
    UserLookupModel userLookupModel() {
        return new UserLookupModel();
    }

//    @Bean
//    MongoTemplate mongoTemplate(MongoClient mongoClient) {
//        return new MongoTemplate(mongoClient , "");
//    }
}
