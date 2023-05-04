package com.example.holidayplanner.budget;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BudgetRepository  extends MongoRepository<Budget, String> {
}
