//package com.example.holidayplanner.group;
//
//import com.example.holidayplanner.models.user.User;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDate;
//import static java.time.Month.*;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.contains;
//import static org.hamcrest.Matchers.not;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class GroupTest {
//
//    @Test
//    void testCanAddMembersToGroup(){
//
//        User user1 = new User(
//                "User",
//                "User",
//                "User",
//                LocalDate.of(2000, JANUARY, 11),
//                "user@user.com",
//                "Password1"
//        );
//
//        User user2 = new User(
//                "User2",
//                "User2",
//                "User2",
//                LocalDate.of(2000, JANUARY, 10),
//                "user2@user.com",
//                "Password1"
//        );
//
//        ArrayList<String> groupMemberUsernames= new ArrayList<>(List.of(user1.getUserName()));
//
//        Group group = new Group("Ibiza Gang", groupMemberUsernames);
//
//        group.addNewMember(user2.getUserName());
//
//        assertEquals("User2", group.getGroupMemberUsernames().get(1));
//    }
//
//    @Test
//    void testCanRemoveMembersFromGroup(){
//
//        User user1 = new User(
//                "1",
//                "User",
//                "User",
//                LocalDate.of(2000, JANUARY, 21),
//                "user@user.com",
//                "Password1"
//        );
//
//        User user2 = new User(
//                "2",
//                "User2",
//                "User2",
//                LocalDate.of(2000, JANUARY, 15),
//                "user2@user.com",
//                "Password1"
//        );
//
//        User user3 = new User(
//                "3",
//                "User3",
//                "User3",
//                LocalDate.of(2000, JANUARY, 19),
//                "user3@user.com",
//                "Password1"
//        );
//
//        ArrayList<String> groupMemberUsernames = new ArrayList<>(List.of(user1.getUserName(), user2.getUserName(), user3.getUserName()));
//
//        Group group = new Group("Ibiza Gang", groupMemberUsernames);
//
//        group.removeMember(user2.getId());
//
//        assertEquals(2, group.getGroupMemberUsernames().size());
//        assertThat(group.getGroupMemberUsernames(), not(contains(user2.getUserName())));
//    }
//}
