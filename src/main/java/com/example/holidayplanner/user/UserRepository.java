package com.example.holidayplanner.user;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findById(ObjectId userId);

    User findByEmail(String email);
    
    @Query(value = "{ '$or': [ { 'email': ?0 }, { 'userName': ?1 } ] }", exists = true)
    boolean existsByEmailOrUserName(String email, String userName);

    Set<User> findByEmailIn(List<String> emails);
}
