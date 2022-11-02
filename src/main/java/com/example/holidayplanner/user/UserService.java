package com.example.holidayplanner.user;

import com.example.holidayplanner.interfaces.ServiceInterface;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class UserService implements ServiceInterface<User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public ResponseEntity create(User user) {

       //Check if email is already registered
        if( emailExists(user) ) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        //Hash password and set role as user
        String encodedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);


        userRepository.insert(user);
        return ResponseEntity.ok("User created successfully");
    }

    @Override
    public List<User> getAll() { return userRepository.findAll(); }

    @Override
    public String update(String userId, User newUserInfo) {
        ObjectId userIdToObjectId = new ObjectId(userId);

        User currentUserInfo = userRepository.findById(userIdToObjectId);

        if (currentUserInfo == null) {  return "user with id " + userId + " does not exists"; }

        currentUserInfo.setFirstName(newUserInfo.getFirstName());
        currentUserInfo.setLastName(newUserInfo.getLastName());
        currentUserInfo.setDob(newUserInfo.getDob());
        currentUserInfo.setEmail(newUserInfo.getEmail());
        currentUserInfo.setPassword(newUserInfo.getPassword());

        userRepository.save(currentUserInfo);
        return "User has been successfully updated";
    }

    @Override
    public String delete(String userId) {
        var userIdToObjectId = new ObjectId(userId);

        User user = userRepository.findById(userIdToObjectId);

        if (user == null){ return "user with id " + userId + " does not exists"; }

        userRepository.delete(user);
        return "Your account has been deleted";
    }

    private boolean emailExists(User user) {
        User x = userRepository.findByEmail(user.getEmail());
        return x == null ? false : true;
    }


}
