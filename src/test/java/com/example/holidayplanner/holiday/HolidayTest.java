//package com.example.holidayplanner.holiday;
//
//import com.example.holidayplanner.availableDates.AvailableDates;
//import com.example.holidayplanner.budget.Budget;
//import com.example.holidayplanner.group.Group;
//import com.example.holidayplanner.user.User;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Stream;
//
//import static java.time.Month.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class HolidayTest {
//
//    private Holiday holiday;
//    private ArrayList<AvailableDates> dates;
//    private AvailableDates datesForUser1;
//    private AvailableDates datesForUser3;
//    private ArrayList<Budget> budgets;
//
////    public HolidayTest(Holiday holiday, ArrayList<AvailableDates> dates, AvailableDates datesForUser1,
////                       AvailableDates datesForUser3, ArrayList<Budget> budgets) {
////        this.holiday = holiday;
////        this.dates = dates;
////        this.datesForUser1 = datesForUser1;
////        this.datesForUser3 = datesForUser3;
////        this.budgets = budgets;
////    }
//
//
//    private static Stream<Holiday> init() {
//        User user1 = new User(
//                "1",
//                "User",
//                "User",
//                LocalDate.of(2002, MAY, 13),
//                "user@user.com",
//                "Password1"
//        );
//
//        User user2 = new User(
//                "2",
//                "User2",
//                "User2",
//                LocalDate.of(2004, OCTOBER, 21),
//                "user2@user.com",
//                "Password1"
//        );
//
//        User user3 = new User(
//                "3",
//                "User3",
//                "User3",
//                LocalDate.of(2000, JANUARY, 11),
//                "user3@user.com",
//                "Password1"
//        );
//
//
//        ArrayList<User> groupMembers = new ArrayList<>(List.of(user1, user2, user3));
//        ArrayList<User> holidayMakers = new ArrayList<>(List.of(user1, user3));
//
//        Group group = new Group("Holiday Gang", groupMembers);
//
//        Budget budgetForUser1 = new Budget(user1,200, 100);
//        Budget budgetForUser3 = new Budget(user3, 300, 200);
//
//        AvailableDates datesForUser1 = new AvailableDates(user1, LocalDate.of(2022, JANUARY, 01), LocalDate.of(2022, JANUARY, 07), 3 );
//        AvailableDates datesForUser3 = new AvailableDates(user1, LocalDate.of(2022, JANUARY, 04), LocalDate.of(2022, JANUARY, 11), 3 );
//
//        ArrayList<Budget> budgets = new ArrayList<Budget>(List.of(budgetForUser1, budgetForUser3));
//        ArrayList<AvailableDates> dates = new ArrayList<>(List.of(datesForUser1, datesForUser3));
//
//        Holiday holiday = new Holiday("Paris Getaway", group, holidayMakers, budgets, dates);
//        return Stream.of(holiday);
//    }
//
//    @ParameterizedTest
//    @MethodSource("init")
//    void testReturnsBudgetInDesiredFormat(Holiday holiday) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
//        var expectedResponse = objectMapper.writeValueAsString(budgets);
//
//        assertEquals(expectedResponse, holiday.getBudget());
//    }
//
//    @ParameterizedTest
//    @MethodSource("init")
//    void testReturnsDatesInDesiredFormat(Holiday holiday) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
//        var expectedResponse = objectMapper.writeValueAsString(dates);
//
//        assertEquals(6, datesForUser1.getNights());
//        assertEquals(7, datesForUser3.getNights());
//        assertEquals(expectedResponse, holiday.getAvailableDates());
//    }
//
//    @ParameterizedTest
//    @MethodSource("init")
//    void testAggregateHolidayBudget(Holiday holiday) {
//        String expectedResponse[] = new String[]{"129.29", "200.0", "270.71"};
//
//        assertEquals(expectedResponse[0], holiday.aggregateHolidayBudgets()[0]);
//    }
//
//    @Test
//    void testAggregateDates() {
//
//        User user1 = new User(
//                "1",
//                "User",
//                "User",
//                LocalDate.of(2002, MAY, 13),
//                "user@user.com",
//                "Password1"
//        );
//
//        User user2 = new User(
//                "2",
//                "User2",
//                "User2",
//                LocalDate.of(2004, OCTOBER, 21),
//                "user2@user.com",
//                "Password1"
//        );
//
//        User user3 = new User(
//                "3",
//                "User3",
//                "User3",
//                LocalDate.of(2000, JANUARY, 11),
//                "user3@user.com",
//                "Password1"
//        );
//
//
//        ArrayList<User> groupMembers = new ArrayList<>(List.of(user1, user2, user3));
//        ArrayList<User> holidayMakers = new ArrayList<>(List.of(user1, user3));
//
//        Group group = new Group("Holiday Gang", groupMembers);
//
//        Budget budgetForUser1 = new Budget(user1,200, 100);
//        Budget budgetForUser3 = new Budget(user3, 300, 200);
//
//        AvailableDates datesForUser1 = new AvailableDates(user1, LocalDate.of(2022, JANUARY, 01), LocalDate.of(2022, JANUARY, 07), 3 );
//        AvailableDates datesForUser3 = new AvailableDates(user1, LocalDate.of(2022, JANUARY, 04), LocalDate.of(2022, JANUARY, 11), 3 );
//
//        ArrayList<Budget> budgets = new ArrayList<Budget>(List.of(budgetForUser1, budgetForUser3));
//        ArrayList<AvailableDates> dates = new ArrayList<>(List.of(datesForUser1, datesForUser3));
//
//        Holiday holiday = new Holiday("Paris Getaway", group, holidayMakers, budgets, dates);
//
//        assertEquals(4, holiday.aggregateDates());
//    }
//}
