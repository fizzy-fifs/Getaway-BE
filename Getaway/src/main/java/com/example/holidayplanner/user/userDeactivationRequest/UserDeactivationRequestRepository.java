package com.example.holidayplanner.user.userDeactivationRequest;

import com.example.holidayplanner.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeactivationRequestRepository extends MongoRepository<UserDeactivationRequest, String> {
    UserDeactivationRequest findByUser(User user);
}
