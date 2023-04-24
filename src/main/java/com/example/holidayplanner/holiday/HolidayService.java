package com.example.holidayplanner.holiday;

import com.example.holidayplanner.group.Group;
import com.example.holidayplanner.group.GroupRepository;
import com.example.holidayplanner.interfaces.ServiceInterface;
import com.example.holidayplanner.user.User;
import com.example.holidayplanner.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HolidayService implements ServiceInterface<Holiday> {
    @Autowired
    private final HolidayRepository holidayRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final ObjectMapper mapper;

    public HolidayService(HolidayRepository holidayRepository, UserRepository userRepository, GroupRepository groupRepository, ObjectMapper mapper) {
        this.holidayRepository = holidayRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<Object> create(Holiday holiday) throws JsonProcessingException {

        Iterable<String> holidayMakersId = holiday.getHolidayMakersIds().stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<User> holidayMakers = (List<User>) userRepository.findAllById(holidayMakersId);

        if (holidayMakers.size() != holiday.getHolidayMakersIds().size()) {
            return ResponseEntity.badRequest().body("One of the userIds added is invalid");
        }

        String groupId = holiday.getGroupId();

        if (groupId == null) {
            return ResponseEntity.badRequest().body("Group id is null");
        }

        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) {
            return ResponseEntity.badRequest().body("Group with group id" + groupId + "does not exists");
        }

        Holiday newHoliday = holidayRepository.insert(holiday);

        group.addHoliday(newHoliday.getId());
        Group savedGroup = groupRepository.save(group);

        for (User holidayMaker : holidayMakers) {
            holidayMaker.addHoliday(newHoliday.getId());
        }

        userRepository.saveAll(holidayMakers);

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
        return holiday.aggregateHolidayBudgets();
    }

    public String[] aggregateDates(String holidayId) {
        Holiday holiday = holidayRepository.findById(holidayId).get();
        return holiday.aggregateDates();
    }

    @Override
    public List<Holiday> getAll() {
        return null;
    }

    @Override
    public String update(String entityId, Holiday newEntityInfo) {
        return null;
    }

    @Override
    public String delete(String entityId) {
        return null;
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
}
