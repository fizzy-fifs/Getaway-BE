package com.example.holidayplanner.holiday;

import com.example.holidayplanner.group.Group;
import com.example.holidayplanner.group.GroupRepository;
import com.example.holidayplanner.interfaces.ServiceInterface;
import com.example.holidayplanner.user.User;
import com.example.holidayplanner.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HolidayService implements ServiceInterface<Holiday> {
    private final HolidayRepository holidayRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public HolidayService(HolidayRepository holidayRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.holidayRepository = holidayRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public ResponseEntity<Object> create(Holiday holiday) throws JsonProcessingException {

        //Find holidaymakers
        List<String> holidayMakersId = holiday.getHolidayMakersIds(); //.stream().map(User::getId).collect(Collectors.toList());
        List<User> holidayMakers = userRepository.findByIdIn(holidayMakersId);

        if (holidayMakers.size() != holiday.getHolidayMakersIds().size()) {
            return ResponseEntity.badRequest().body("One of the userIds added is invalid");
        }

        //Find group
        String groupId = holiday.getGroupId();
        Group group = groupRepository.findById(new ObjectId(groupId));

        if (group == null) {
            return ResponseEntity.badRequest().body("Group with group id" + groupId + "does not exists");
        }

        //Insert holiday in DB
        Holiday newHoliday = holidayRepository.insert(holiday);

        //Add holiday to group object and save group in DB
        group.addHoliday(newHoliday.getId());
        Group savedGroup = groupRepository.save(group);

        //Add holiday to user objects and save users in DB
        for (User holidayMaker : holidayMakers) {
            holidayMaker.addHoliday(newHoliday.getId());
        }

        userRepository.saveAll(holidayMakers);

        //Convert holiday object to json
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        String holidayJson = mapper.writeValueAsString(newHoliday);

        return ResponseEntity.ok(holidayJson);
    }

    public ResponseEntity<Object> addHolidayMaker(String holidayId, String userId) {
        User newHolidayMaker = userRepository.findById(new ObjectId(userId));

        if (newHolidayMaker == null) {
            return ResponseEntity.badRequest().body("User with user id" + userId + "does not exists");
        }

        Holiday holiday = holidayRepository.findById(holidayId).get();
        holiday.addHolidayMaker(newHolidayMaker.getId());

        holidayRepository.save(holiday);

        return ResponseEntity.ok(newHolidayMaker.getFirstName() + " has been successfully added to " + holiday.getName());
    }

    public String removeHolidayMaker(String holidayId, String userId) {
        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) { return "User with user id" + userId + "does not exists"; }

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
}
