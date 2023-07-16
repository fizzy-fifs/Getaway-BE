package com.example.holidayplanner.groupInvite;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GroupInviteService {
    @Autowired
    private final GroupInviteRepository groupInviteRepository;

    public GroupInviteService(GroupInviteRepository groupInviteRepository) {
        this.groupInviteRepository = groupInviteRepository;
    }

    public ResponseEntity findGroupInviteById(String groupInviteId) {
        if (groupInviteId == null) {
            return ResponseEntity.badRequest().body("No group invite id provided");
        }

        GroupInvite groupInvite = groupInviteRepository.findById(new ObjectId(groupInviteId));

        if (groupInvite == null) {
            return ResponseEntity.badRequest().body("No group invite found");
        }

        return ResponseEntity.ok().body(groupInvite);
    }
}
