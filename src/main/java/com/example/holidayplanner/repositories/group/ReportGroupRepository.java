package com.example.holidayplanner.repositories.group;

import com.example.holidayplanner.models.group.ReportGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportGroupRepository extends MongoRepository<ReportGroup, String> {

}
