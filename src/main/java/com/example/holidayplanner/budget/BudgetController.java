package com.example.holidayplanner.budget;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/budgets")
@Api(tags = "Budgets")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class BudgetController {

    @Autowired
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping(path = "/findmultiplebyid")
    @ApiOperation(value = "Find multiple budgets by id")
    public ResponseEntity findMultipleById(@RequestBody List<String> budgetIds) throws JsonProcessingException {
        return budgetService.findMultipleById(budgetIds);
    }
}
