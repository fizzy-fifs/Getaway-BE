package com.example.holidayplanner.holiday;

import com.example.holidayplanner.availableDates.AvailableDates;
import com.example.holidayplanner.availableDates.AvailableDatesRepository;
import com.example.holidayplanner.budget.Budget;
import com.example.holidayplanner.budget.BudgetRepository;
import com.example.holidayplanner.group.Group;
import com.example.holidayplanner.group.GroupRepository;
import com.example.holidayplanner.interfaces.ServiceInterface;
import com.example.holidayplanner.user.User;
import com.example.holidayplanner.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HolidayService {
    @Autowired
    private final HolidayRepository holidayRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final ObjectMapper mapper;

    @Autowired
    private final BudgetRepository budgetRepository;

    @Autowired
    private final AvailableDatesRepository availableDatesRepository;

    public HolidayService(HolidayRepository holidayRepository, UserRepository userRepository, GroupRepository groupRepository, ObjectMapper mapper, BudgetRepository budgetRepository, AvailableDatesRepository availableDatesRepository) {
        this.holidayRepository = holidayRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.mapper = mapper;
        this.budgetRepository = budgetRepository;
        this.availableDatesRepository = availableDatesRepository;
    }


    public ResponseEntity<Object> create(Holiday holiday, Budget budget, AvailableDates availableDates) throws JsonProcessingException {

        List<String> invitedHolidayMakersIds = holiday.getInvitedHolidayMakersIds();
        List<User> invitedHolidayMakers = (List<User>) userRepository.findAllById(invitedHolidayMakersIds);

        if (invitedHolidayMakers.size() != invitedHolidayMakersIds.size()) {
            return ResponseEntity.badRequest().body("One of the userIds added is invalid");
        }

        String groupId = holiday.getGroupId();

        if (groupId == null || groupId.isEmpty()) {
            return ResponseEntity.badRequest().body("Group id is null or empty. Please add a valid group id");
        }

        Group group ;

        try {
            group = groupRepository.findById(new ObjectId(groupId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Group id is invalid. Please add a valid group id");
        }

        Budget newBudget = budgetRepository.insert(budget);
        AvailableDates newAvailableDates = availableDatesRepository.insert(availableDates);

        holiday.getBudgetIds().add(newBudget.getId());
        holiday.getAvailableDatesIds().add(newAvailableDates.getId());

        Holiday newHoliday = holidayRepository.insert(holiday);

        group.addHoliday(newHoliday.getId());

        for (User invitedHolidayMaker : invitedHolidayMakers) {
            invitedHolidayMaker.getHolidayInvites().add(newHoliday.getId());
        }

        userRepository.saveAll(invitedHolidayMakers);
        groupRepository.save(group);

        String holidayJson = mapper.writeValueAsString(newHoliday);

        return ResponseEntity.ok(holidayJson);
    }

    public ResponseEntity<Object> addHolidayMaker(String holidayId, String userId) {
        User newHolidayMaker = userRepository.findById(new ObjectId(userId));

        if (newHolidayMaker == null) {
            return ResponseEntity.badRequest().body("User with user id" + userId + "does not exists");
        }

        Holiday holiday = holidayRepository.findById(new ObjectId(holidayId));
        holiday.addHolidayMaker(newHolidayMaker.getId());

        holidayRepository.save(holiday);

        return ResponseEntity.ok(newHolidayMaker.getFirstName() + " has been successfully added to " + holiday.getName());
    }

    public String removeHolidayMaker(String holidayId, String userId) {
        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return "User with user id" + userId + "does not exists";
        }

        Holiday holiday = holidayRepository.findById(holidayId).get();
        holiday.removeHolidayMaker(user.getId());

        return user.getFirstName() + " has been removed from " + holiday.getName();
    }

    public String[] aggregateHolidayBudgets(String holidayId) {
        Holiday holiday = holidayRepository.findById(holidayId).get();

        List<String> budgetIds = holiday.getBudgetIds();
        List<Budget> budgets = (List<Budget>) budgetRepository.findAllById(budgetIds);

        return aggregateHolidayBudgets(budgets);
    }

    public String[] aggregateDates(String holidayId) {
        Holiday holiday = holidayRepository.findById(holidayId).get();

        List<String> availableDatesIds = holiday.getAvailableDatesIds();
        List<AvailableDates> availableDates = (List<AvailableDates>) availableDatesRepository.findAllById(availableDatesIds);

        return aggregateDates(availableDates);
    }

    public ResponseEntity<Object> findMultipleById(List<String> holidayIds) throws JsonProcessingException {

        if (holidayIds == null || holidayIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No holiday id provided");
        }

        List<Holiday> holidays = (List<Holiday>) holidayRepository.findAllById(holidayIds);

        if (holidays.isEmpty()) {
            return ResponseEntity.badRequest().body("No holiday found");
        }

        if (holidays.size() != holidayIds.size()) {
            return ResponseEntity.badRequest().body("One of the holidayIds added is invalid");
        }

        String holidaysJson = mapper.writeValueAsString(holidays);

        return ResponseEntity.ok(holidaysJson);
    }

    public ResponseEntity<Object> findById(String holidayId) throws JsonProcessingException {

        if (holidayId == null || holidayId.isEmpty()) {
            return ResponseEntity.badRequest().body("No holiday id provided");
        }

        Holiday holiday = holidayRepository.findById(new ObjectId(holidayId));

        if (holiday == null) {
            return ResponseEntity.badRequest().body("No holiday found");
        }

        String holidayJson = mapper.writeValueAsString(holiday);

        return ResponseEntity.ok(holidayJson);
    }

    private String[] aggregateHolidayBudgets(List<Budget> budgets) {

        double[] medianBudget = new double[budgets.size()];
        for (int i = 0; i < budgets.size(); i++) {
            var median = calculateMedian(budgets.get(i).getBudgetUpperLimit(),
                    budgets.get(i).getBudgetLowerLimit());
            medianBudget[i] = median;
        }

        var findMean = new Mean();
        var average = findMean.evaluate(medianBudget);

        var findSd = new StandardDeviation();
        var sd = findSd.evaluate(medianBudget);

        DecimalFormat df = new DecimalFormat("0.00");
        return new String[]{df.format(average - sd), df.format(average), df.format(average + sd)};
    }

    private String[] aggregateDates(List<AvailableDates> availableDates) {

        double[] startDatesArray = new double[availableDates.size()];
        double[] endDatesArray = new double[availableDates.size()];

        for (int i = 0; i < availableDates.size(); i++) {
            var startDateInMilli = ((double) availableDates.get(i).getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            var endDateInMilli = ((double) availableDates.get(i).getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

            startDatesArray[i] = startDateInMilli;
            endDatesArray[i] = endDateInMilli;
        }
        var findMean = new Mean();
        var averageStartDate = (long) findMean.evaluate(startDatesArray);
        var averageEndDate = (long) findMean.evaluate(endDatesArray);

        var findSd = new StandardDeviation();
        var sdOfStartDates = (long) findSd.evaluate(startDatesArray);
        var sdOfEndDates = (long) findSd.evaluate(endDatesArray);

        LocalDate suggestedStartDate1 = Instant.ofEpochMilli(averageStartDate - sdOfStartDates)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedStartDate2 = Instant.ofEpochMilli(averageStartDate)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedStartDate3 = Instant.ofEpochMilli(averageStartDate + sdOfStartDates)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedEndDate1 = Instant.ofEpochMilli(averageEndDate - sdOfEndDates)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedEndDate2 = Instant.ofEpochMilli(averageEndDate)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedEndDate3 = Instant.ofEpochMilli(averageEndDate + sdOfEndDates)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        return new String[]{
                suggestedStartDate1 + "-" + suggestedEndDate1,
                suggestedStartDate2 + "-" + suggestedEndDate2,
                suggestedStartDate3 + "-" + suggestedEndDate3
        };
    }

    private double calculateMedian(double upperLimit, double lowerLimit) {

        var range = (upperLimit - lowerLimit) + 1;
        var medianIndex = Math.floor(range / 2);

        if (range % 2 != 0) {
            return lowerLimit + medianIndex;
        } else {
            return ((lowerLimit + medianIndex) + (lowerLimit + medianIndex) - 1) / 2;
        }
    }
}
