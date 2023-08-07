package com.example.holidayplanner.holidayInvite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolidayInviteService {

    @Autowired
    private final HolidayInviteRepository holidayInviteRepository;

    @Autowired
    private final ObjectMapper objectMapper;

    public HolidayInviteService(HolidayInviteRepository holidayInviteRepository, ObjectMapper objectMapper) {
        this.holidayInviteRepository = holidayInviteRepository;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity findMultipleById(List<String> holidayInviteIds) throws JsonProcessingException {
        if (holidayInviteIds == null || holidayInviteIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No holiday invite ids provided");
        }

        Iterable<HolidayInvite> holidayInvite = holidayInviteRepository.findAllById(holidayInviteIds);

        if (holidayInvite == null) {
            return ResponseEntity.badRequest().body("No holiday invites found");
        }

        String holidayInviteJson = objectMapper.writeValueAsString(holidayInvite);

        return ResponseEntity.ok().body(holidayInviteJson);
    }
}
