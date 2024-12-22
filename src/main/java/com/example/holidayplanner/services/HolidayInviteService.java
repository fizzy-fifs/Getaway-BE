package com.example.holidayplanner.services;

import com.example.holidayplanner.helpers.CacheHelper;
import com.example.holidayplanner.repositories.HolidayInviteRepository;
import com.example.holidayplanner.models.HolidayInvite;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HolidayInviteService {
    private final HolidayInviteRepository holidayInviteRepository;
    @Autowired
    private CacheHelper<HolidayInvite> holidayInviteCacheHelper;
    private final String CACHE_NAME = "holiday_invite";
    private final ObjectMapper objectMapper;

    public HolidayInviteService(HolidayInviteRepository holidayInviteRepository, ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        this.holidayInviteRepository = holidayInviteRepository;
        this.objectMapper = objectMapper;
        this.holidayInviteCacheHelper = new CacheHelper<>(redisTemplate, objectMapper, HolidayInvite.class);
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
        List<HolidayInvite> cachedHolidayInvites = holidayInviteCacheHelper.getCachedEntries(CACHE_NAME, holidayInviteIds);

        List<String> idsToFetch = holidayInviteIds.stream().filter(id -> cachedHolidayInvites.stream().noneMatch(holidayInvite -> holidayInvite.getId().equals(id))).toList();

        if (idsToFetch.isEmpty()) {
            return cachedHolidayInvites;
        }

        List<HolidayInvite> freshHolidayInvites = holidayInviteRepository.findAllById(idsToFetch);

        holidayInviteCacheHelper.cacheEntries(CACHE_NAME, freshHolidayInvites, HolidayInvite::getId);
        List<HolidayInvite> allHolidayInvites = new ArrayList<>(cachedHolidayInvites);
        allHolidayInvites.addAll(freshHolidayInvites);

        return allHolidayInvites;
    }
}
