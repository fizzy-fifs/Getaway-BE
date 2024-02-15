package com.example.holidayplanner.user;

import com.example.holidayplanner.scheduler.Scheduler;
import com.example.holidayplanner.user.reportUser.ReportUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User")
@SecurityRequirement(name = "holidayPlannerSecurity")
public class UserController {

    private final UserService userService;

    private final Scheduler scheduler;

    @Autowired
    public UserController(UserService userService, Scheduler scheduler) {
        this.userService = userService;
        this.scheduler = scheduler;
    }

    @PostMapping(path = "/newuser")
    @Operation(summary = "Create a new user")
    public ResponseEntity create(@RequestBody @Valid User user, Errors errors) throws JsonProcessingException {

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors().get(0).getDefaultMessage());
        }
        return userService.create(user);
    }

    @PostMapping(path = "/login")
    @Operation(summary = "Authenticate users")
    @Transactional
    public ResponseEntity login(@RequestBody Map<String, String> emailAndPassword) throws JsonProcessingException {

        return userService.login(emailAndPassword);
    }

    @GetMapping(path = "/logout/{userId}")
    @Operation(summary = "Logout a user")
    public ResponseEntity logout(@PathVariable("userId") String userId) {
        return userService.logout(userId);
    }

    @GetMapping
    @Operation(summary = "Get a list of all users")
    public List<User> getAll() {
        return userService.getAll();
    }


    @GetMapping(path = "deactivate/{userId}")
    @Operation(summary = "Deactivate a user's account")
    public ResponseEntity<String> deactivateUserAccount(@PathVariable("userId") String userId) {
        return userService.deactivateUserAccount(userId);
    }

    @GetMapping(path = "/sendfriendrequest/{userId}/{allegedFriendId}")
    @Operation(summary = "Send a friend request")
    public ResponseEntity sendFriendRequest(@PathVariable String userId, @PathVariable String allegedFriendId) {
        return userService.sendFriendRequest(userId, allegedFriendId);
    }

    @GetMapping(path = "/acceptfriendrequest/{userId}/{friendId}")
    @Operation(summary = "Add a Friend")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) {
        return userService.acceptFriendRequest(userId, friendId);
    }

    @GetMapping(path = "/declinefriendrequest/{userId}/{friendId}")
    @Operation(summary = "Delete a friend request")
    public ResponseEntity<String> deleteFriendRequest(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) throws JsonProcessingException {
        return userService.deleteFriendRequest(userId, friendId);
    }

    @GetMapping(path = "withdrawfriendrequest/{userId}/{friendId}")
    @Operation(summary = "Withdraw a friend request")
    public ResponseEntity<String> withdrawFriendRequest(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) throws JsonProcessingException {
        return userService.withdrawFriendRequest(userId, friendId);
    }

    @GetMapping(path = "/findbyid/{id}")
    @Operation(summary = "Find a user by their id")
    public ResponseEntity findById(@PathVariable("id") String id) throws JsonProcessingException {
        return userService.findById(id);
    }

    @PostMapping(path = "/findmultiplebyid")
    @Operation(summary = "Find multiple users by their ids")
    public ResponseEntity findMultipleById(@RequestBody List<String> userIds) throws JsonProcessingException {
        return userService.findMultipleById(userIds);
    }

    @PostMapping(path = "/findmultiplebyphonenumberoremail")
    @Operation(summary = "Find multiple users by their phone numbers or email addresses")
    public ResponseEntity findMultipleByPhoneNumberOrEmail(@RequestBody Map<String, List<String>> phoneNumbersAndEmails) throws JsonProcessingException {
        return userService.findMultipleByPhoneNumberOrEmail(phoneNumbersAndEmails);
    }

    @GetMapping(path = "/search/{searchTerm}/{userId}")
    @Operation(summary = "Search for a user")
    public ResponseEntity<Object> search(@PathVariable("searchTerm") String searchTerm, @PathVariable("userId") String userId) throws JsonProcessingException {
        return userService.search(searchTerm, userId);
    }

    @PostMapping(path = "savedevicetoken/{userId}")
    @Operation(summary = "Save a user's device token")
    public ResponseEntity saveDeviceToken(@PathVariable("userId") String userId, @RequestBody String deviceToken) {
        return userService.saveDeviceToken(userId, deviceToken);
    }

    @PutMapping(path = "/{userId}")
    @Operation(summary = "Update user details")
    public String update(@PathVariable("userId") String userId, @RequestBody User newUserInfo) {
        return userService.update(userId, newUserInfo);
    }

    @GetMapping(path = "/updateuserproperties")
    @Operation(summary = "Update user properties in database")
    public ResponseEntity<Object> updateUserproperties() {
        scheduler.updateUserPropertiesIfNotPresent();
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/report")
    @Operation(summary = "Report a user")
    public ResponseEntity<Object> reportUser(@RequestBody ReportUser reportUser) throws JsonProcessingException {
        return userService.reportUser(reportUser);
    }

    @GetMapping(path = "/reactivate/{userId}")
    @Operation(summary = "Reactivate a user's account")
    public ResponseEntity<Object> reactivateUserAccount(@PathVariable("userId") String userId) throws JsonProcessingException {
        return userService.reactivateUserAccount(userId);
    }

    @GetMapping(path = "/block")
    @Operation(summary = "Block a user")
    public ResponseEntity<Object> blockUser(@RequestParam(name = "authenticatedUserId") String authenticatedUserId, @RequestParam(name = "userId") String userId) throws JsonProcessingException {
        return userService.blockUser(authenticatedUserId, userId);
    }

    @GetMapping(path = "/unblock")
    @Operation(summary = "Unblock a user")
    public ResponseEntity<Object> unblockUser(@RequestParam(name = "authenticatedUserId") String authenticatedUserId, @RequestParam(name = "userId") String blockedUserId) throws JsonProcessingException {
        return userService.unblockUser(authenticatedUserId, blockedUserId);
    }
}