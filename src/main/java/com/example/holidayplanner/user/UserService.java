package com.example.holidayplanner.user;

import com.example.holidayplanner.config.MyUserDetailsService;
import com.example.holidayplanner.config.jwt.JwtUtil;
import com.example.holidayplanner.config.jwt.refreshToken.RefreshToken;
import com.example.holidayplanner.config.jwt.refreshToken.RefreshTokenRepository;
import com.example.holidayplanner.interfaces.ServiceInterface;
import com.example.holidayplanner.user.role.RoleRepository;
import com.example.holidayplanner.userLookupModel.UserLookupModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

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
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private final MongoTemplate mongoTemplate;

    @Autowired
    private final ObjectMapper mapper;

    private final int pageNumber = 0;

    private final int pageSize = 10;


    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, MyUserDetailsService myUserDetailsService, JwtUtil jwtTokenUtil, AuthenticationManager authenticationManager, RefreshTokenRepository refreshTokenRepository, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.mongoTemplate = mongoTemplate;
        this.mapper = new ObjectMapper().findAndRegisterModules();;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public ResponseEntity<Object> create(User user) throws JsonProcessingException {

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

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setOwner(user);
        RefreshToken savedRefreshToken = refreshTokenRepository.insert(refreshToken);

        //Generate JWT
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(user.getEmail());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        String refreshTokenString = jwtTokenUtil.generateRefreshToken(userDetails, savedRefreshToken.getId());

        //Put JWT and created user object in a map and send response
        Map<String, Object> responseData = new HashMap<>();

            //Convert user object to json
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        String userJson = mapper.writeValueAsString(savedUser);

        responseData.put("user", userJson);
        responseData.put("jwt", jwt);
        responseData.put("refreshToken", refreshTokenString);

        return ResponseEntity.ok(responseData);
    }

    @Transactional
    public ResponseEntity<Object> login(Map<String, String> emailAndPassword) throws JsonProcessingException {

        //Retrieve email and password as separate strings
        var email = emailAndPassword.get("email");
        var password = emailAndPassword.get("password");

        //Authenticate using authentication manager
        try {
            authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(email, password) );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        User user = userRepository.findByEmail(email);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setOwner(user);
        RefreshToken savedRefreshToken = refreshTokenRepository.insert(refreshToken);

        //Generate JWT
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);
        String jwt = jwtTokenUtil.generateToken(userDetails);
        String refreshTokenString = jwtTokenUtil.generateRefreshToken(userDetails, savedRefreshToken.getId());

        //Send user object and JWT as response


            //convert user object to json format
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        String userJson = objectMapper.writeValueAsString(user);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("user", userJson);
        responseData.put("jwt", jwt);
        responseData.put("refreshToken", refreshTokenString);

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

//    public ResponseEntity<?> logout(@RequestBody Map<String, String> tokens) {
//        String jwt = tokens.get("jwt");
//        String refreshToken = tokens.get("refreshToken");
//      Check jwt and refresh tokens are valid, then delete the refresh token from the db.
//    }

    private boolean emailExists(User user) {
        User findUser = userRepository.findByEmail(user.getEmail());
        return findUser != null;
    }

    private boolean userNameExists(User user) {
        User findUser = userRepository.findByUserName(user.getUserName().toLowerCase());
        return findUser != null;
    }

    public ResponseEntity<Object> sendFriendRequest(String userId, String allegedFriendId) {

        ArrayList<User> users;

        try {
            users = (ArrayList<User>) userRepository.findAllById(Arrays.asList(userId,allegedFriendId)); //Returns Iterable list of users in random order
        }catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Id");
        }

        if (users.size() != 2) { return ResponseEntity.badRequest().body("One or more of the Ids is/are invalid"); }

        User allegedFriend = null;
        User principal = null;

        for (User user : users) {

            if (Objects.equals(user.getId(), allegedFriendId)) {
                allegedFriend = user;
            }
            else if (Objects.equals(user.getId(), userId)) {
                principal = user;
            }

        }

        assert allegedFriend != null;
        assert principal != null;
        allegedFriend.addFriendRequest(principal.getId());

        userRepository.save(allegedFriend);
        return ResponseEntity.ok(allegedFriend.getFirstName() + " has been sent a friend request");
    }

    public ResponseEntity<Object> acceptFriendRequest(String userId, String friendId) {
        // query db for both users using userId and friendId
        ArrayList<User> users;

        try {
            users = (ArrayList<User>) userRepository.findAllById(Arrays.asList(userId, friendId)); //Returns Iterable list of users in random order
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Id");
        }

        if (users.size() != 2) { return ResponseEntity.badRequest().body("One or more of the Ids is/are invalid"); }

        User friend = null;
        User principal = null;

        for (User user : users) {

            if (Objects.equals(user.getId(), friendId)) {
                friend = user;
            }
            else if (Objects.equals(user.getId(), userId)) {
                principal = user;
            }

        }

        //Check request exists in principal's friend request list
        assert principal != null;
        List<String> friendRequests = principal.getFriendRequests();

        assert friend != null;
        if ( !friendRequests.contains(friend.getId()) ) { return ResponseEntity.badRequest().body("Friend Request does not exist"); }



        //Add friend to principal's friend's list and vice versa
        principal.addFriend(friend.getId());
        friend.addFriend(principal.getId());

        //Remove the request from the friendRequest list and save to db
        friendRequests.remove(friendId);
        List<User> savedUsers = userRepository.saveAll(Arrays.asList(principal,friend));

        return ResponseEntity.ok(savedUsers);
    }

    public ResponseEntity findMultipleByPhoneNumberOrEmail(Map<String, List<String>> phoneNumbersAndEmails) throws JsonProcessingException {

        List<User> users;

        try {
            UserLookupModel userLookup = mapper.convertValue(phoneNumbersAndEmails, UserLookupModel.class);
            users = userRepository.findByPhoneNumberInOrEmailIn(userLookup.phoneNumbers, userLookup.emails);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Request Format. Request body must be a JSON object with phoneNumbers and emails properties");
        }
        String usersJson = mapper.writeValueAsString(users);
        return ResponseEntity.ok(usersJson);
    }

    public ResponseEntity<Object> deleteFriendRequest(String userId, String friendId) {
        User user = userRepository.findById(new ObjectId(userId));

        user.deleteFriendRequest(friendId);
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(savedUser);
    }


    public ResponseEntity<List<User>> search(String searchTerm) {
        String sanitizedSearchTerm = searchTerm.trim().toLowerCase();

        Query searchQuery = new Query();
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("firstName").regex(sanitizedSearchTerm, "i"),
                Criteria.where("lastName").regex(sanitizedSearchTerm, "i"),
                Criteria.where("userName").regex(sanitizedSearchTerm, "i"),
                Criteria.where("email").regex(sanitizedSearchTerm, "i")
        );

        searchQuery.addCriteria(criteria);
        searchQuery.with(PageRequest.of(pageNumber, pageSize));


        List<User> users = mongoTemplate.find(searchQuery, User.class);
        return ResponseEntity.ok(users);
    }

    public ResponseEntity findById(String id) throws JsonProcessingException {

        User user = userRepository.findById(new ObjectId(id));

        if (user == null) { return ResponseEntity.badRequest().body("User with id " + id + " does not exist"); }

        String userJson = mapper.writeValueAsString(user);
        return ResponseEntity.ok(userJson);
    }

    public ResponseEntity findMultipleById(List<String> ids) throws JsonProcessingException {
        if (ids.isEmpty()) { return ResponseEntity.badRequest().body("No ids provided"); }

        List<User> users = (List<User>) userRepository.findAllById(ids);

        if (users.isEmpty()) { return ResponseEntity.badRequest().body("No users found"); }

        String usersJson = mapper.writeValueAsString(users);

        return ResponseEntity.ok(usersJson);
    }
}
