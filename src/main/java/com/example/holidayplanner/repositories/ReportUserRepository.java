package com.example.holidayplanner.repositories;

import com.example.holidayplanner.models.user.ReportUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportUserRepository extends MongoRepository<ReportUser, String> {
}
