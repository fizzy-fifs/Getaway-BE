package com.example.holidayplanner.user;

import com.example.holidayplanner.config.MyUserDetailsService;
import com.example.holidayplanner.config.jwt.JwtUtil;
import com.example.holidayplanner.interfaces.ServiceInterface;
import com.example.holidayplanner.user.role.RoleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService implements ServiceInterface<User> {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    private final JwtUtil jwtTokenUtil;

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, MyUserDetailsService myUserDetailsService, JwtUtil jwtTokenUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public ResponseEntity create(User user) throws JsonProcessingException {

       //Check if email is already registered
        if( emailExists(user) ) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        //Check if username already exists
        if(userNameExists(user) ) {
            return ResponseEntity.badRequest().body("Username is already taken.");
        }

        //Hash password and set role as user
        String encodedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));

        //Insert user in DB
        User savedUser = userRepository.insert(user);

        //Generate JWT
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(user.getEmail());
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        //Put JWT and created user object in a map and send response
        Map<String, Object> responseData = new HashMap<>();

            //Convert user object to json
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        String userJson = mapper.writeValueAsString(savedUser);

        responseData.put("user", userJson);
        responseData.put("jwt", jwt);

        return ResponseEntity.ok(responseData);
    }


    public ResponseEntity login(Map<String, String> emailAndPassword) throws JsonProcessingException {

        //Retrieve email and password as separate strings
        var email = emailAndPassword.get("email");
        var password = emailAndPassword.get("password");

        //Authenticate using authentication manager
        try {
            authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(email, password) );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        //Generate JWT
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);
        String jwt = jwtTokenUtil.generateToken(userDetails);

        //Send user object and JWT as response
        User user = userRepository.findByEmail(email);

            //convert user object to json format
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        String userJson = objectMapper.writeValueAsString(user);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("user", userJson);
        responseData.put("jwt", jwt);

        return ResponseEntity.ok(responseData);

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
        User findUser = userRepository.findByEmail(user.getEmail());
        return findUser != null;
    }

    private boolean userNameExists(User user) {
        User findUser = userRepository.findByUserName(user.getUserName());
        return findUser != null;
    };



}
