package com.example.holidayplanner.user;

import com.example.holidayplanner.interfaces.ControllerInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public UserController(UserService userService) { this.userService = userService; }

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
    public ResponseEntity login (@RequestBody @Valid Map<String, String> emailAndPassword, Errors errors) throws JsonProcessingException {

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(errors.getAllErrors()
                    .get(0).getDefaultMessage())
            ;
        }

        return userService.login(emailAndPassword);
    }

    @Override
    @GetMapping
    @ApiOperation(value = "Get a list of all users")
    public List<User> getAll() { return userService.getAll(); }

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
}
