package com.example.holidayplanner.services;

import com.example.holidayplanner.repositories.GroupInviteRepository;
import com.example.holidayplanner.helpers.CacheHelper;
import com.example.holidayplanner.models.GroupInvite;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupInviteService {
    @Autowired
    private final GroupInviteRepository groupInviteRepository;

    private final CacheHelper<GroupInvite> groupInviteCacheHelper;

    @Autowired
    private final ObjectMapper objectMapper;

    public GroupInviteService(GroupInviteRepository groupInviteRepository, ObjectMapper objectMapper, RedisTemplate<String, GroupInvite> redisTemplate) {
        this.groupInviteRepository = groupInviteRepository;
        this.objectMapper = objectMapper;
        this.groupInviteCacheHelper = new CacheHelper<>(redisTemplate);
    }

    public ResponseEntity<String> findById(String groupInviteId) throws JsonProcessingException {
        if (groupInviteId == null) {
            return ResponseEntity.badRequest().body("No group invite id provided");
        }

        GroupInvite groupInvite = findSingleGroupInviteByIdInCacheOrDatabase(groupInviteId);

        if (groupInvite == null) {
            return ResponseEntity.badRequest().body("No group invite found");
        }

        String groupInviteJson = objectMapper.writeValueAsString(groupInvite);

        return ResponseEntity.ok().body(groupInviteJson);
    }

    public ResponseEntity<String> findMultipleById(List<String> groupInviteIds) throws JsonProcessingException {
        if (groupInviteIds == null || groupInviteIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No group invite ids provided");
        }

        List<GroupInvite> groupInvites = findMultipleGroupInvitesByIdInCacheOrDatabase(groupInviteIds);

        if (groupInvites.isEmpty()) {
            return ResponseEntity.badRequest().body("No group invites found");
        }

        String groupInvitesJson = objectMapper.writeValueAsString(groupInvites);

        return ResponseEntity.ok().body(groupInvitesJson);
    }

    @Cacheable(value = "group invites", key = "#id", unless = "#result == null")
    public GroupInvite findSingleGroupInviteByIdInCacheOrDatabase(String groupInviteId) {
        return groupInviteRepository.findById(new ObjectId(groupInviteId));
    }

    public List<GroupInvite> findMultipleGroupInvitesByIdInCacheOrDatabase(List<String> groupInviteIds) {
        List<GroupInvite> cachedGroupInvites = groupInviteCacheHelper.getCachedEntries(groupInviteIds);

        List<String> idsToFetch = groupInviteIds.stream().filter(id -> cachedGroupInvites.stream().noneMatch(gi -> gi.getId().equals(id))).toList();

        if (idsToFetch.isEmpty()) {
            return cachedGroupInvites;
        }

        List<GroupInvite> freshGroupInvites = groupInviteRepository.findAllById(idsToFetch);

        groupInviteCacheHelper.cacheEntries(freshGroupInvites, GroupInvite::getId);
        List<GroupInvite> allGroupInvites = new ArrayList<>(cachedGroupInvites);
        allGroupInvites.addAll(freshGroupInvites);

        return allGroupInvites;
    }
}
