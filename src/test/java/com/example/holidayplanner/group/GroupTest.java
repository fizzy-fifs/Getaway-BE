package com.example.holidayplanner.group;

import com.example.holidayplanner.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import static java.time.Month.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupTest {

    @Test
    void testCanAddMembersToGroup(){

        User user1 = new User(
                "User",
                "User",
                "User",
                LocalDate.of(2000, JANUARY, 11),
                "user@user.com",
                "Password1"
        );

        User user2 = new User(
                "User2",
                "User2",
                "User2",
                LocalDate.of(2000, JANUARY, 10),
                "user2@user.com",
                "Password1"
        );

        ArrayList<User> groupMembers = new ArrayList<>(List.of(user1));

        Group group = new Group("Ibiza Gang", groupMembers);

        group.addNewMember(user2);

        assertEquals("User2", group.getGroupMembers().get(1).getFirstName());
    }

    @Test
    void testCanRemoveMembersFromGroup(){

        User user1 = new User(
                "1",
                "User",
                "User",
                LocalDate.of(2000, JANUARY, 21),
                "user@user.com",
                "Password1"
        );

        User user2 = new User(
                "2",
                "User2",
                "User2",
                LocalDate.of(2000, JANUARY, 15),
                "user2@user.com",
                "Password1"
        );

        User user3 = new User(
                "3",
                "User3",
                "User3",
                LocalDate.of(2000, JANUARY, 19),
                "user3@user.com",
                "Password1"
        );

        ArrayList<User> groupMembers = new ArrayList<>(List.of(user1, user2, user3));

        Group group = new Group("Ibiza Gang", groupMembers);

        group.removeMember(user2.getId());

        assertEquals(2, group.getGroupMembers().size());
        assertThat(group.getGroupMembers(), not(contains(user2)));
    }
}
