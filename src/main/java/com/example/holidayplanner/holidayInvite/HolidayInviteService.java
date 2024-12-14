package com.example.holidayplanner.holidayInvite;

import com.example.holidayplanner.helpers.CacheHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HolidayInviteService {

    @Autowired
    private final HolidayInviteRepository holidayInviteRepository;

    private final CacheHelper<HolidayInvite> holidayInviteCacheHelper;

    @Autowired
    private final ObjectMapper objectMapper;

    public HolidayInviteService(HolidayInviteRepository holidayInviteRepository, ObjectMapper objectMapper) {
        this.holidayInviteRepository = holidayInviteRepository;
        this.objectMapper = objectMapper;
        holidayInviteCacheHelper = new CacheHelper<>();
    }

    public ResponseEntity<String> findMultipleById(List<String> holidayInviteIds) throws JsonProcessingException {
        if (holidayInviteIds == null || holidayInviteIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No holiday invite ids provided");
        }

        List<HolidayInvite> holidayInvite = findMultipleGroupInvitesByIdInCacheOrDatabase(holidayInviteIds);

        if (holidayInvite.isEmpty()) {
            return ResponseEntity.badRequest().body("No holiday invites found");
        }

        String holidayInviteJson = objectMapper.writeValueAsString(holidayInvite);

        return ResponseEntity.ok().body(holidayInviteJson);
    }

    public List<HolidayInvite> findMultipleGroupInvitesByIdInCacheOrDatabase(List<String> holidayInviteIds) {
        List<HolidayInvite> cachedHolidayInvites = holidayInviteCacheHelper.getCachedEntries(holidayInviteIds);

        List<String> idsToFetch = holidayInviteIds.stream().filter(id -> cachedHolidayInvites.stream().noneMatch(holidayInvite -> holidayInvite.getId().equals(id))).toList();

        if (idsToFetch.isEmpty()) {
            return cachedHolidayInvites;
        }

        List<HolidayInvite> freshHolidayInvites = holidayInviteRepository.findAllById(idsToFetch);

        holidayInviteCacheHelper.cacheEntries(freshHolidayInvites, HolidayInvite::getId);
        List<HolidayInvite> allHolidayInvites = new ArrayList<>(cachedHolidayInvites);
        allHolidayInvites.addAll(freshHolidayInvites);

        return allHolidayInvites;
    }
}
