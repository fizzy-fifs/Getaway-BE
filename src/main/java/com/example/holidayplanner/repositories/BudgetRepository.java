package com.example.holidayplanner.repositories;

import com.example.holidayplanner.models.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BudgetRepository  extends MongoRepository<Budget, String> {
}
