package com.example.holidayplanner.services;

import com.example.holidayplanner.models.AvailableDates;
import com.example.holidayplanner.repositories.AvailableDatesRepository;
import com.example.holidayplanner.models.Budget;
import com.example.holidayplanner.repositories.BudgetRepository;
import com.example.holidayplanner.models.Holiday;
import com.example.holidayplanner.repositories.HolidayRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Service
public class HolidayPreferencesService {

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private AvailableDatesRepository availableDatesRepository;

    @Autowired
    private ObjectMapper mapper;

    public HolidayPreferencesService(HolidayRepository holidayRepository, BudgetRepository budgetRepository, AvailableDatesRepository availableDatesRepository, ObjectMapper mapper) {
        this.holidayRepository = holidayRepository;
        this.budgetRepository = budgetRepository;
        this.availableDatesRepository = availableDatesRepository;
        this.mapper = mapper;
    }


    public ResponseEntity create(String holidayId, @Valid Budget budget, @Valid AvailableDates availableDates) throws JsonProcessingException {
        Holiday holiday = holidayRepository.findById(new ObjectId(holidayId));

        if (holiday == null) {
            return ResponseEntity.badRequest().body("Holiday not found");
        }

        Budget savedBudget =  budgetRepository.save(budget);
        AvailableDates savedAvailableDates = availableDatesRepository.save(availableDates);

        holiday.addBudget(savedBudget.getId());
        holiday.addAvailableDates(savedAvailableDates.getId());

        Holiday savedHoliday = holidayRepository.save(holiday);
        String savedHolidayJson = mapper.writeValueAsString(savedHoliday);

        return ResponseEntity.ok().body(savedHolidayJson);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(errorMessage);
    }
}
