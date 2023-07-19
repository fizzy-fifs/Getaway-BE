package com.example.holidayplanner.budget;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BudgetRepository  extends MongoRepository<Budget, String> {
}
