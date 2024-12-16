package com.example.holidayplanner.services;

import com.example.holidayplanner.repositories.BudgetRepository;
import com.example.holidayplanner.models.Budget;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private final BudgetRepository budgetRepository;

    @Autowired
    private final ObjectMapper mapper;


    public BudgetService(BudgetRepository budgetRepository, ObjectMapper mapper) {
        this.budgetRepository = budgetRepository;
        this.mapper = mapper;
    }


    public ResponseEntity findMultipleById(List<String> budgetIds) throws JsonProcessingException {

            if (budgetIds.isEmpty()) {
                return ResponseEntity.badRequest().body("No budget provided");
            }

            List<Budget> budgets = (List<Budget>) budgetRepository.findAllById(budgetIds);

            if (budgets.isEmpty()) {
                return ResponseEntity.badRequest().body("No budgets found");
            }

            if (budgets.size() != budgetIds.size()) {
                return ResponseEntity.badRequest().body("One of the budgets provided is invalid");
            }

            String budgetsJson = mapper.writeValueAsString(budgets);

            return ResponseEntity.ok().body(budgetsJson);
    }
}
