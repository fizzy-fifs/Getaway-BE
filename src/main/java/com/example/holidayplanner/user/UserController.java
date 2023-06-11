package com.example.holidayplanner.user;

import com.example.holidayplanner.interfaces.ControllerInterface;
import com.example.holidayplanner.scheduler.Scheduler;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1.0/users")
@Api(tags = "User")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class UserController implements ControllerInterface<User> {

    private final UserService userService;

    private Scheduler scheduler;

    @Autowired
    public UserController(UserService userService, Scheduler scheduler) {
        this.userService = userService;
        this.scheduler = scheduler;
    }

    @Override
    @PostMapping(path = "/newuser")
    @ApiOperation(value = "Create a new user")
    public ResponseEntity create(@RequestBody @Valid User user, Errors errors) throws JsonProcessingException {

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(errors.getAllErrors()
                    .get(0).getDefaultMessage())
            ;
        }
        return userService.create(user);
    }

    @PostMapping(path = "/login")
    @ApiOperation(value = "Authenticate users")
    @Transactional
    public ResponseEntity login (@RequestBody  Map<String, String> emailAndPassword) throws JsonProcessingException {

        return userService.login(emailAndPassword);
    }

    @Override
    @GetMapping
    @ApiOperation(value = "Get a list of all users")
    public List<User> getAll() { return userService.getAll(); }

    @GetMapping(path="/sendfriendrequest/{userId}/{allegedFriendId}")
    @ApiOperation(value = "Send a friend request")
    public ResponseEntity sendFriendRequest(@PathVariable String userId, @PathVariable String allegedFriendId) {
        return userService.sendFriendRequest(userId, allegedFriendId);
    }

    @GetMapping(path="/acceptfriendrequest/{userId}/{friendId}")
    @ApiOperation("Add a Friend")
    public ResponseEntity acceptFriendRequest(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) {
        return userService.acceptFriendRequest(userId, friendId);
    }

    @GetMapping(path = "/deleteFriendRequest/{userId}/{friendId}")
    @ApiOperation("Delete a friend request")
    public ResponseEntity deleteFriendRequest(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) {
        return userService.deleteFriendRequest(userId, friendId);
    }

    @GetMapping(path= "/findbyid/{id}")
    @ApiOperation(value = "Find a user by their id")
    public ResponseEntity findById(@PathVariable("id") String id) throws JsonProcessingException {
        return userService.findById(id);
    }

    @PostMapping(path="/findmultiplebyid")
    @ApiOperation(value = "Find multiple users by their ids")
    public ResponseEntity findMultipleById(@RequestBody List<String> userIds) throws JsonProcessingException {
        return userService.findMultipleById(userIds);
    }

    @PostMapping(path="/findmultiplebyphonenumberoremail")
    @ApiOperation(value = "Find multiple users by their phone numbers or email addresses")
    public ResponseEntity findMultipleByPhoneNumberOrEmail(@RequestBody Map<String, List<String>> phoneNumbersAndEmails) throws JsonProcessingException {
        return userService.findMultipleByPhoneNumberOrEmail(phoneNumbersAndEmails);
    }

    @GetMapping(path = "/search/{searchTerm}")
    @ApiOperation(value = "Search for a user")
    public ResponseEntity<List<User>> search(@PathVariable("searchTerm") String searchTerm) throws JsonProcessingException {
        return userService.search(searchTerm);
    }

    @PostMapping(path = "savedevicetoken/$userId")
    @ApiOperation(value = "Save a user's device token")
    public ResponseEntity saveDeviceToken(@PathVariable("userId") String userId, @RequestBody String deviceToken) {
        return userService.saveDeviceToken(userId, deviceToken);
    }

    @Override
    @PutMapping (path = "/{userId}")
    @ApiOperation(value = "Update user details")
    public String update(@PathVariable("userId") String userId, @RequestBody User newUserInfo) {
        return userService.update(userId, newUserInfo);
    }

    @Override
    @DeleteMapping(path = "/{userId}")
    @ApiOperation(value = "Delete a user")
    public String delete(@PathVariable("userId") String userId) {
        return userService.delete(userId);
    }

    @GetMapping(path = "/updateuserproperties")
    @ApiOperation(value = "Update user properties in database")
    public ResponseEntity<Object> updateUserproperties() {
        scheduler.updateUserPropertiesIfNotPresent();
        return ResponseEntity.ok().build();
    }

}
