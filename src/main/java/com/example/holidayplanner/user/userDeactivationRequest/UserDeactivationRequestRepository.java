package com.example.holidayplanner.user.userDeactivationRequest;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeactivationRequestRepository extends MongoRepository<UserDeactivationRequest, String> {
}
