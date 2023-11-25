package com.example.holidayplanner.user;

import com.example.holidayplanner.config.MyUserDetailsService;
import com.example.holidayplanner.config.jwt.JwtUtil;
import com.example.holidayplanner.config.jwt.token.Token;
import com.example.holidayplanner.config.jwt.token.TokenService;
import com.example.holidayplanner.helpers.Helper;
import com.example.holidayplanner.user.reportUser.ReportUser;
import com.example.holidayplanner.user.reportUser.ReportUserRepository;
import com.example.holidayplanner.user.role.RoleRepository;
import com.example.holidayplanner.user.userDeactivationRequest.UserDeactivationRequest;
import com.example.holidayplanner.user.userDeactivationRequest.UserDeactivationRequestRepository;
import com.example.holidayplanner.userLookupModel.UserLookupModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

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
    private final TokenService tokenService;

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final MongoTemplate mongoTemplate;

    @Autowired
    private final ObjectMapper mapper;

    @Autowired
    private final UserDeactivationRequestRepository userDeactivationRequestRepository;

    @Autowired
    private final ReportUserRepository reportUserRepository;

    private final int pageNumber = 0;

    private final int pageSize = 10;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, MyUserDetailsService myUserDetailsService, JwtUtil jwtTokenUtil, TokenService tokenService, AuthenticationManager authenticationManager, MongoTemplate mongoTemplate, UserDeactivationRequestRepository userDeactivationRequestRepository, ReportUserRepository reportUserRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.mongoTemplate = mongoTemplate;
        this.userDeactivationRequestRepository = userDeactivationRequestRepository;
        this.reportUserRepository = reportUserRepository;
        this.mapper = new ObjectMapper().findAndRegisterModules();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public ResponseEntity<Object> create(User user) throws JsonProcessingException {

        //Check if email is already registered
        if (emailExists(user)) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        //Check if username already exists
        if (userNameExists(user)) {
            return ResponseEntity.badRequest().body("Username is already taken.");
        }

        user.setFirstName(Helper.toSentenceCase(user.getFirstName()));
        user.setLastName(Helper.toSentenceCase(user.getLastName()));
        user.setUserName(user.getUserName().toLowerCase());
        user.setEmail(user.getEmail().toLowerCase());

        user.setDateJoined(LocalDate.now());
        user.setLastLogin(LocalDateTime.now());


        //Hash password and set role as user
        String encodedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));

        //Insert user in DB
        User savedUser = userRepository.insert(user);

        //Generate JWT
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(user.getEmail());
        final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        Token token = new Token();
        token.setOwner(savedUser);
        token.setAccessToken(accessToken);
        token.setAccessTokenExpiration(jwtTokenUtil.extractExpiration(accessToken));
        token.setRefreshToken(refreshToken);
        token.setRefreshTokenExpiration(jwtTokenUtil.extractExpiration(refreshToken));

        tokenService.saveToken(token);

        //Put JWT and created user object in a map and send response
        Map<String, Object> responseData = new HashMap<>();

        //Convert user object to json
        String userJson = mapper.writeValueAsString(savedUser);

        responseData.put("user", userJson);
        responseData.put("accessToken", accessToken);
        responseData.put("refreshToken", refreshToken);

        return ResponseEntity.ok(responseData);
    }

    @Transactional
    public ResponseEntity<Object> login(Map<String, String> emailAndPassword) throws JsonProcessingException {

        String email = emailAndPassword.get("email");
        String password = emailAndPassword.get("password");

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest().body("User with email " + email + " does not exist");
        }

        tokenService.deleteAllByOwner(user);

        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);
        final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        Token token = new Token();
        token.setOwner(user);
        token.setAccessToken(accessToken);
        token.setAccessTokenExpiration(jwtTokenUtil.extractExpiration(accessToken));
        token.setRefreshToken(refreshToken);
        token.setRefreshTokenExpiration(jwtTokenUtil.extractExpiration(refreshToken));


        tokenService.saveToken(token);

        String userJson = mapper.writeValueAsString(user);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("user", userJson);
        responseData.put("accessToken", accessToken);
        responseData.put("refreshToken", refreshToken);

        if (!user.isActive()) {
            var deactivationRequest = userDeactivationRequestRepository.findByUser(user);
            responseData.put("deactivationRequest", mapper.writeValueAsString(deactivationRequest));
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok(responseData);

    }

    public ResponseEntity logout(String userId) {
        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("User with id " + userId + " does not exist");
        }

        tokenService.deleteAllByOwner(user);

        return ResponseEntity.ok("You have been logged out");
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public String update(String userId, User newUserInfo) {
        ObjectId userIdToObjectId = new ObjectId(userId);

        User currentUserInfo = userRepository.findById(userIdToObjectId);

        if (currentUserInfo == null) {
            return "user with id " + userId + " does not exists";
        }

        currentUserInfo.setFirstName(newUserInfo.getFirstName());
        currentUserInfo.setLastName(newUserInfo.getLastName());
        currentUserInfo.setEmail(newUserInfo.getEmail());
        currentUserInfo.setPassword(newUserInfo.getPassword());

        userRepository.save(currentUserInfo);
        return "User has been successfully updated";
    }

    public ResponseEntity<String> deactivateUserAccount(String userId) {
        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("User does not exist");
        }

        var deactivationRequest = userDeactivationRequestRepository.findByUser(user);

        if (deactivationRequest != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Your account is already scheduled for deactivation");
        }

        user.setActive(false);

        UserDeactivationRequest userDeactivationRequest = new UserDeactivationRequest(user, LocalDateTime.now());

        userDeactivationRequestRepository.save(userDeactivationRequest);
        userRepository.save(user);

        return ResponseEntity.ok("Your account will be deactivated in 30 days");
    }


    public ResponseEntity<Object> sendFriendRequest(String userId, String allegedFriendId) {

        ArrayList<User> users;

        try {
            users = (ArrayList<User>) userRepository.findAllById(Arrays.asList(userId, allegedFriendId)); //Returns Iterable list of users in random order
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Id");
        }

        if (users.size() != 2) {
            return ResponseEntity.badRequest().body("One or more of the Ids is/are invalid");
        }

        User allegedFriend = null;
        User principal = null;

        for (User user : users) {

            if (Objects.equals(user.getId(), allegedFriendId)) {
                allegedFriend = user;
            } else if (Objects.equals(user.getId(), userId)) {
                principal = user;
            }

        }

        assert allegedFriend != null;
        assert principal != null;

        if (allegedFriend.getBlockedUserIds().contains(principal.getId())) {
            return ResponseEntity.badRequest().body("You have been blocked by " + allegedFriend.getFirstName() + ". You cannot send a friend request to this user");
        }

        if (allegedFriend.getFriendRequestsSent().contains(principal.getId())) {
            return ResponseEntity.badRequest().body("You have already sent a friend request to " + allegedFriend.getFirstName());
        }

        if (allegedFriend.getFriends().contains(principal.getId())) {
            return ResponseEntity.badRequest().body("You are already friends with " + allegedFriend.getFirstName());
        }

        allegedFriend.addFriendRequest(principal.getId());
        principal.addToFriendRequestsSent(allegedFriend.getId());

        userRepository.saveAll(Arrays.asList(allegedFriend, principal));
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

        if (users.size() != 2) {
            return ResponseEntity.badRequest().body("One or more of the Ids is/are invalid");
        }

        User friend = null;
        User principal = null;

        for (User user : users) {

            if (Objects.equals(user.getId(), friendId)) {
                friend = user;
            } else if (Objects.equals(user.getId(), userId)) {
                principal = user;
            }

        }

        //Check request exists in principal's friend request list
        assert principal != null;
        List<String> friendRequests = principal.getFriendRequests();

        assert friend != null;
        if (!friendRequests.contains(friend.getId())) {
            return ResponseEntity.badRequest().body("Friend Request does not exist");
        }


        //Add friend to principal's friend's list and vice versa
        principal.addFriend(friend.getId());
        friend.addFriend(principal.getId());

        //Remove the request from the friendRequest list, friendRequestSent list and save to db
        friendRequests.remove(friendId);
        friend.removeFromFreindRequestsSent(principal.getId());
        List<User> savedUsers = userRepository.saveAll(Arrays.asList(principal, friend));

        return ResponseEntity.ok("You are now friends with " + friend.getFirstName());
    }

    public ResponseEntity findMultipleByPhoneNumberOrEmail(Map<String, List<String>> phoneNumbersAndEmails) throws JsonProcessingException {
        System.out.println(phoneNumbersAndEmails);
        List<User> users;

        try {
            UserLookupModel userLookup = mapper.convertValue(phoneNumbersAndEmails, UserLookupModel.class);
            users = userRepository.findByPhoneNumberInOrEmailIn(userLookup.getPhoneNumbers(), userLookup.getEmails()); // Replace with query.

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Request Format. Request body must be a JSON object with phoneNumbers and emails properties");
        }

        System.out.println(users);

        String usersJson = mapper.writeValueAsString(users);
        return ResponseEntity.ok(usersJson);
    }

    public ResponseEntity<Object> declineFriendRequest(String userId, String friendId) {
        List<User> users;

        try {
            users = (List<User>) userRepository.findAllById(Arrays.asList(userId, friendId)); //Returns Iterable list of users in random order
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Id");
        }

        if (users.size() != 2) {
            return ResponseEntity.badRequest().body("One or more of the Ids is/are invalid");
        }

        for (User user : users) {
            user.deleteFriendRequest(friendId);
            user.removeFromFreindRequestsSent(userId);
        }

        List<User> savedUsers = userRepository.saveAll(users);

        return ResponseEntity.ok(savedUsers);
    }


    public ResponseEntity<Object> search(String searchTerm, String userId) {
        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("User with id " + userId + " does not exist");
        }

        String sanitizedSearchTerm = searchTerm.trim().toLowerCase();

        List<Object> recentUserSearchHistory = user.getRecentUserSearchHistory();

        recentUserSearchHistory.remove(sanitizedSearchTerm);

        recentUserSearchHistory.add(0, sanitizedSearchTerm);

        if (recentUserSearchHistory.size() > 10) {
            recentUserSearchHistory.remove(recentUserSearchHistory.size() - 1);
        }

        user.setRecentUserSearchHistory(recentUserSearchHistory);

        Query searchQuery = new Query();
        Criteria criteria = new Criteria().orOperator(Criteria.where("firstName").regex(sanitizedSearchTerm, "i"), Criteria.where("lastName").regex(sanitizedSearchTerm, "i"), Criteria.where("userName").regex(sanitizedSearchTerm, "i"), Criteria.where("email").regex(sanitizedSearchTerm, "i"));

        searchQuery.addCriteria(criteria);
        int pageSize = 10;
        searchQuery.with(PageRequest.of(pageNumber, pageSize));


        List<User> users = mongoTemplate.find(searchQuery, User.class);
        users.remove(user);
        users.removeIf(u -> u.getEmail().equals("admin@mail.com"));
        userRepository.save(user);
        return ResponseEntity.ok(users);
    }

    public ResponseEntity findById(String id) throws JsonProcessingException {

        User user = userRepository.findById(new ObjectId(id));

        if (user == null) {
            return ResponseEntity.badRequest().body("User with id " + id + " does not exist");
        }

        String userJson = mapper.writeValueAsString(user);
        return ResponseEntity.ok(userJson);
    }

    public ResponseEntity findMultipleById(List<String> userIds) throws JsonProcessingException {
        if (userIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No ids provided");
        }

        List<User> users = (List<User>) userRepository.findAllById(userIds);

        if (users.isEmpty()) {
            return ResponseEntity.badRequest().body("No users found");
        }

        String usersJson = mapper.writeValueAsString(users);

        return ResponseEntity.ok(usersJson);
    }

    public ResponseEntity saveDeviceToken(String userId, String deviceToken) {
        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("User with id " + userId + " does not exist");
        }

        user.setDeviceToken(deviceToken);
        userRepository.save(user);

        return ResponseEntity.ok("Device token saved");
    }

    public ResponseEntity<Object> reportUser(ReportUser reportUser) {
        if (reportUser.getUserToReport() == null || reportUser.getUserReporting() == null) {
            return ResponseEntity.badRequest().body("User to report and user reporting cannot be null");
        }

        if (reportUser.getReason() == null || reportUser.getReason().isEmpty()) {
            return ResponseEntity.badRequest().body("Reason cannot be null or empty");
        }

        if (reportUser.getUserToReport().getId().equals(reportUser.getUserReporting().getId())) {
            return ResponseEntity.badRequest().body("You cannot report yourself");
        }

        List<User> users = (List<User>) userRepository.findAllById(Arrays.asList(reportUser.getUserToReport().getId(), reportUser.getUserReporting().getId()));

        if (users.size() != 2) {
            return ResponseEntity.badRequest().body("One or more of the Ids is/are invalid");
        }

        User userToReport = null;
        User userReporting = null;

        for (User user : users) {

            if (Objects.equals(user.getId(), reportUser.getUserToReport().getId())) {
                userToReport = user;
            } else if (Objects.equals(user.getId(), reportUser.getUserReporting().getId())) {
                userReporting = user;
            }
        }

        assert userToReport != null;
        assert userReporting != null;

        ReportUser newReportUser = new ReportUser(userToReport, userReporting, reportUser.getReason(), LocalDateTime.now());

        ReportUser savedReportUser = reportUserRepository.insert(newReportUser);
        return ResponseEntity.ok(savedReportUser);
    }

    public ResponseEntity<Object> reactivateUserAccount(String userId) throws JsonProcessingException {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body("User id cannot be null or empty");
        }

        User user = userRepository.findById(new ObjectId(userId));

        if (user == null) {
            return ResponseEntity.badRequest().body("User with id " + userId + " does not exist");
        }

        if (user.isActive()) {
            return ResponseEntity.badRequest().body("User is already active");
        }

        user.setActive(true);

        UserDeactivationRequest userDeactivationRequest = userDeactivationRequestRepository.findByUser(user);

        if (userDeactivationRequest != null) {
            userDeactivationRequestRepository.delete(userDeactivationRequest);
        }

        User savedUser = userRepository.save(user);

        var savedUserJson = mapper.writeValueAsString(savedUser);

        return ResponseEntity.ok(savedUserJson);
    }

    public ResponseEntity<Object> blockUser(String authenticatedUserId, String userId) {
        if (authenticatedUserId == null || authenticatedUserId.isEmpty()) {
            return ResponseEntity.badRequest().body("Authenticated user id cannot be null or empty");
        }

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body("User id cannot be null or empty");
        }

        List<User> users = (List<User>) userRepository.findAllById(Arrays.asList(authenticatedUserId, userId));

        if (users.size() != 2) {
            return ResponseEntity.badRequest().body("One or more of the Ids is/are invalid");
        }

        User authenticatedUser = null;
        User userToBlock = null;

        for (User user : users) {

            if (Objects.equals(user.getId(), authenticatedUserId)) {
                authenticatedUser = user;
            } else if (Objects.equals(user.getId(), userId)) {
                userToBlock = user;
            }
        }

        assert authenticatedUser != null;
        assert userToBlock != null;

        if (authenticatedUser.getBlockedUserIds().contains(userToBlock.getId())) {
            return ResponseEntity.badRequest().body("You have already blocked this user");
        }

        authenticatedUser.removeFriend(userToBlock.getId());
        userToBlock.removeFriend(authenticatedUser.getId());

        authenticatedUser.addBlockedUser(userToBlock.getId());
        userToBlock.addBlockedByUser(authenticatedUser.getId());

        userRepository.saveAll(Arrays.asList(authenticatedUser, userToBlock));
        return ResponseEntity.ok("You have blocked " + userToBlock.getUserName());
    }

    public ResponseEntity<Object> unblockUser(String authenticatedUserId, String blockedUserId) {
        if (authenticatedUserId == null || authenticatedUserId.isEmpty()) {
            return ResponseEntity.badRequest().body("Authenticated user id cannot be null or empty");
        }

        if (blockedUserId == null || blockedUserId.isEmpty()) {
            return ResponseEntity.badRequest().body("User id cannot be null or empty");
        }

        List<User> users = (List<User>) userRepository.findAllById(Arrays.asList(authenticatedUserId, blockedUserId));

        if (users.size() != 2) {
            return ResponseEntity.badRequest().body("One or more of the Ids is/are invalid");
        }

        User authenticatedUser = null;
        User blockedUser = null;

        for (User user : users) {

            if (Objects.equals(user.getId(), authenticatedUserId)) {
                authenticatedUser = user;
            } else if (Objects.equals(user.getId(), blockedUserId)) {
                blockedUser = user;
            }
        }

        assert authenticatedUser != null;
        assert blockedUser != null;

        if (!authenticatedUser.getBlockedUserIds().contains(blockedUser.getId())) {
            return ResponseEntity.badRequest().body("You have not blocked this user");
        }

        authenticatedUser.removeBlockedUser(blockedUser.getId());
        blockedUser.removeBlockedByUser(authenticatedUser.getId());

        userRepository.saveAll(Arrays.asList(authenticatedUser, blockedUser));
        return ResponseEntity.ok("You have unblocked " + blockedUser.getUserName());
    }

    private boolean emailExists(User user) {
        User findUser = userRepository.findByEmail(user.getEmail());
        return findUser != null;
    }

    private boolean userNameExists(User user) {
        User findUser = userRepository.findByUserName(user.getUserName().toLowerCase());
        return findUser != null;
    }
}
