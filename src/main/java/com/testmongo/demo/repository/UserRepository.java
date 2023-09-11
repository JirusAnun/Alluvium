package com.testmongo.demo.repository;

import com.testmongo.demo.collection.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {


    @Override
    Optional<User> findById(String s);

    Optional<User> findByUsername(String username);
}
