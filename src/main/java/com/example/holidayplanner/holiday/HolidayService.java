package com.example.holidayplanner.holiday;

import com.example.holidayplanner.availableDates.AvailableDates;
import com.example.holidayplanner.availableDates.AvailableDatesRepository;
import com.example.holidayplanner.budget.Budget;
import com.example.holidayplanner.budget.BudgetRepository;
import com.example.holidayplanner.group.Group;
import com.example.holidayplanner.group.GroupRepository;
import com.example.holidayplanner.holidayInvite.HolidayInvite;
import com.example.holidayplanner.holidayInvite.HolidayInviteRepository;
import com.example.holidayplanner.user.User;
import com.example.holidayplanner.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private HolidayInviteRepository holidayInviteRepository;

    public HolidayService(HolidayRepository holidayRepository, UserRepository userRepository, GroupRepository groupRepository, ObjectMapper mapper, BudgetRepository budgetRepository, AvailableDatesRepository availableDatesRepository, HolidayInviteRepository holidayInviteRepository) {
        this.holidayRepository = holidayRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.mapper = mapper;
        this.budgetRepository = budgetRepository;
        this.availableDatesRepository = availableDatesRepository;
        this.holidayInviteRepository = holidayInviteRepository;
    }


    public ResponseEntity<Object> create(Holiday holiday, Budget budget, AvailableDates availableDates) throws JsonProcessingException {

        List<String> userIdsToCheck = new ArrayList<>();

        holiday.getInvitedHolidayMakers().stream().map(User::getId).forEach(userIdsToCheck::add);

        String holidayCreatorId = holiday.getHolidayMakers().get(0).getId();
        userIdsToCheck.add(holidayCreatorId);

        List<User> confirmedUsers = (List<User>) userRepository.findAllById(userIdsToCheck);

        if (confirmedUsers.size() != userIdsToCheck.size()) {
            System.out.println(confirmedUsers);
            return ResponseEntity.badRequest().body("One of the user ids added is invalid");
        }

        String groupId = holiday.getGroupId();

        if (groupId == null) {
            return ResponseEntity.badRequest().body("Group id is null or empty. Please add a valid group id");
        }

        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) {
            return ResponseEntity.badRequest().body("Group id is invalid. Please add a valid group id");
        }

        Budget newBudget = budgetRepository.insert(budget);
        holiday.getBudgetIds().add(newBudget.getId());

        AvailableDates newAvailableDates = availableDatesRepository.insert(availableDates);
        holiday.getAvailableDatesIds().add(newAvailableDates.getId());

        holiday.setId(new ObjectId().toString());

        group.addHoliday(holiday);

        User inviter = confirmedUsers.stream()
                .filter(holidayMaker ->
                        holidayMaker.getId()
                        .equals(holidayCreatorId))
                .findFirst().get();

        HolidayInvite holidayInvite = new HolidayInvite(holiday, inviter);
        HolidayInvite newHolidayInvite = holidayInviteRepository.insert(holidayInvite);

        for (User invitedHolidayMaker : confirmedUsers) {
            if (!invitedHolidayMaker.getId().equals(inviter.getId())) {
                invitedHolidayMaker.getHolidayInviteIds().add(newHolidayInvite.getId());
            }
        }

        inviter.addHoliday(holiday.getId());

        userRepository.saveAll(confirmedUsers);
        groupRepository.save(group);
        Holiday newHoliday = holidayRepository.save(holiday);

        String holidayJson = mapper.writeValueAsString(newHoliday);

        return ResponseEntity.ok(holidayJson);
    }

    public String removeHolidayMaker(String holidayId, String userId) {
        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return "User with user id" + userId + "does not exists";
        }

        Holiday holiday = holidayRepository.findById(new ObjectId(holidayId));
        holiday.removeHolidayMaker(user.getId());

        return user.getFirstName() + " has been removed from " + holiday.getName();
    }

    public ResponseEntity getDateAndBudgetAggregates(String holidayId) throws JsonProcessingException {
        if (holidayId == null || holidayId.isEmpty()) {
            return ResponseEntity.badRequest().body("Holiday id is null or empty. Please add a valid holiday id");
        }

        Holiday holiday = holidayRepository.findById(new ObjectId(holidayId));

        if (holiday == null) {
            return ResponseEntity.badRequest().body("Holiday id is invalid. Please add a valid holiday id");
        }

        List<String> budgetIdsToCheck = holiday.getBudgetIds();

        List<Budget> budgets = (List<Budget>) budgetRepository.findAllById(budgetIdsToCheck);

        if (budgets.isEmpty() || budgets.size() != budgetIdsToCheck.size()) {
            return ResponseEntity.badRequest().body("No budgets found. Please enter valid budget ids");
        }

        List<String> availableDatesIdsToCheck = holiday.getAvailableDatesIds();

        List<AvailableDates> availableDates = (List<AvailableDates>) availableDatesRepository.findAllById(availableDatesIdsToCheck);
        if (availableDates.isEmpty() || availableDates.size() != availableDatesIdsToCheck.size()) {
            return ResponseEntity.badRequest().body("No dates found. Please enter valid date ids");
        }

        String[] budgetAggregates = aggregateBudgets(budgets);
        String[] dateAggregates = aggregateDates(availableDates);

        Map<String, String[]> aggregates = Map.of("budgetAggregates", budgetAggregates, "dateAggregates", dateAggregates);

        return ResponseEntity.ok().body(mapper.writeValueAsString(aggregates));
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

    public ResponseEntity<Object> acceptInvite(String holidayInviteId, String userId) {
        if (holidayInviteId == null || holidayInviteId.isEmpty()) {
            return ResponseEntity.badRequest().body("No holiday invite id provided");
        }

        HolidayInvite holidayInvite = holidayInviteRepository.findById(new ObjectId(holidayInviteId));

        if (holidayInvite == null) {
            return ResponseEntity.badRequest().body("Invite does not exist");
        }

        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("No user found");
        }

        Holiday holiday = holidayInvite.getHoliday();

        if (!holiday.getInvitedHolidayMakers().contains(user)) {
            return ResponseEntity.badRequest().body("Unfortunately, you have not been invited to this holiday");
        }

        holiday.removeInvitedHolidayMaker(user.getId());
        user.deleteHolidayInvite(holidayInvite.getId());

        holiday.addHolidayMaker(user);
        user.addHoliday(holiday.getId());

        holidayRepository.save(holiday);
        userRepository.save(user);

        return ResponseEntity.ok("You have successfully accepted the invite");
    }

    public ResponseEntity declineInvite(String holidayInviteId, String userId) {
        HolidayInvite holidayInvite = holidayInviteRepository.findById(new ObjectId(holidayInviteId));

        if (holidayInvite == null) {
            return ResponseEntity.badRequest().body("Invite does not exist");
        }

        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("No user found");
        }

        Holiday holiday = holidayInvite.getHoliday();

        if (!holiday.getInvitedHolidayMakers().contains(user)) {
            return ResponseEntity.badRequest().body("Unfortunately, you have not been invited to this holiday");
        }

        holiday.removeInvitedHolidayMaker(user.getId());
        user.deleteHolidayInvite(holidayInvite.getId());

        holidayRepository.save(holiday);
        userRepository.save(user);

        return ResponseEntity.ok("Invitation declined");
    }

    private String[] aggregateBudgets(List<Budget> budgets) {

        double[] medianBudget = new double[budgets.size()];
        for (int i = 0; i < budgets.size(); i++) {
            var median = calculateMedian(budgets.get(i).getBudgetUpperLimit(), budgets.get(i).getBudgetLowerLimit());
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

        LocalDate suggestedStartDate1 = Instant.ofEpochMilli(averageStartDate - sdOfStartDates).atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedStartDate2 = Instant.ofEpochMilli(averageStartDate).atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedStartDate3 = Instant.ofEpochMilli(averageStartDate + sdOfStartDates).atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedEndDate1 = Instant.ofEpochMilli(averageEndDate - sdOfEndDates).atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedEndDate2 = Instant.ofEpochMilli(averageEndDate).atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate suggestedEndDate3 = Instant.ofEpochMilli(averageEndDate + sdOfEndDates).atZone(ZoneId.systemDefault()).toLocalDate();

        return new String[]{suggestedStartDate1 + "-" + suggestedEndDate1, suggestedStartDate2 + "-" + suggestedEndDate2, suggestedStartDate3 + "-" + suggestedEndDate3};
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
