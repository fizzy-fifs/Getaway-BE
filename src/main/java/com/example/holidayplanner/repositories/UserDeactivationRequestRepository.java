package com.example.holidayplanner.repositories;

import com.example.holidayplanner.models.user.User;
import com.example.holidayplanner.models.user.UserDeactivationRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeactivationRequestRepository extends MongoRepository<UserDeactivationRequest, String> {
    UserDeactivationRequest findByUser(User user);
}
