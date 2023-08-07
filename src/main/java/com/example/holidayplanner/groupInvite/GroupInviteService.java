package com.example.holidayplanner.groupInvite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupInviteService {
    @Autowired
    private final GroupInviteRepository groupInviteRepository;

    @Autowired
    private final ObjectMapper objectMapper;

    public GroupInviteService(GroupInviteRepository groupInviteRepository, ObjectMapper objectMapper) {
        this.groupInviteRepository = groupInviteRepository;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity findById(String groupInviteId) throws JsonProcessingException {
        if (groupInviteId == null) {
            return ResponseEntity.badRequest().body("No group invite id provided");
        }

        GroupInvite groupInvite = groupInviteRepository.findById(new ObjectId(groupInviteId));

        if (groupInvite == null) {
            return ResponseEntity.badRequest().body("No group invite found");
        }

        String groupInviteJson = objectMapper.writeValueAsString(groupInvite);

        return ResponseEntity.ok().body(groupInviteJson);
    }

    public ResponseEntity findMultipleById(List<String> groupInviteIds) throws JsonProcessingException {
        if (groupInviteIds == null || groupInviteIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No group invite ids provided");
        }

        Iterable<GroupInvite> groupInvites = groupInviteRepository.findAllById(groupInviteIds);

        if (groupInvites == null) {
            return ResponseEntity.badRequest().body("No group invites found");
        }

        String groupInvitesJson = objectMapper.writeValueAsString(groupInvites);

        return ResponseEntity.ok().body(groupInvitesJson);
    }
}
