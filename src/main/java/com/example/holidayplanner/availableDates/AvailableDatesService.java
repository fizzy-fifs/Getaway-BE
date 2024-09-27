package com.example.holidayplanner.availableDates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvailableDatesService {

    @Autowired
    private final AvailableDatesRepository availableDatesRepository;

    @Autowired
    private final ObjectMapper mapper;

    public AvailableDatesService(AvailableDatesRepository availableDatesRepository, ObjectMapper mapper) {
        this.availableDatesRepository = availableDatesRepository;
        this.mapper = mapper;
    }


    public ResponseEntity findMultipleById(List<String> availableDatesIds) throws JsonProcessingException {

        if (availableDatesIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No date provided");
        }

        List<AvailableDates> availableDates = (List<AvailableDates>) availableDatesRepository.findAllById(availableDatesIds);

        if (availableDates.isEmpty()) {
            return ResponseEntity.badRequest().body("No dates found");
        }

        if (availableDates.size() != availableDatesIds.size()) {
            return ResponseEntity.badRequest().body("One of the dates provided is invalid");
        }

        String availableDatesJson = mapper.writeValueAsString(availableDates);

        return ResponseEntity.ok().body(availableDatesJson);
    }
}
