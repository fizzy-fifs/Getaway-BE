package com.example.holidayplanner.user;

import com.example.holidayplanner.interfaces.ControllerInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1.0/users")
public class UserController implements ControllerInterface<User> {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) { this.userService = userService; }

    @Override
    @PostMapping(path = "/newuser")
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
    public List<User> getAll() { return userService.getAll(); }

    @Override
    @PutMapping (path = "/{userId}")
    public String update(@PathVariable("userId") String userId, @RequestBody User newUserInfo) {
        return userService.update(userId, newUserInfo);
    }

    @Override
    @DeleteMapping(path = "/{userId}")
    public String delete(@PathVariable("userId") String userId) {
        return userService.delete(userId);
    }
}
