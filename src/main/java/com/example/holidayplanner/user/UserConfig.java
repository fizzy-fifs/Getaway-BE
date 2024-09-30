package com.example.holidayplanner.user;

import com.example.holidayplanner.userLookupModel.UserLookupModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
}
