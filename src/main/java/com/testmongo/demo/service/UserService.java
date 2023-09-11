package com.testmongo.demo.service;

import com.testmongo.demo.collection.User;
import com.testmongo.demo.repository.UserRepository;
import com.testmongo.demo.exception.BaseException;
import com.testmongo.demo.exception.UserException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findbyUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public void create(String username, String password, String confirmpasword) throws BaseException {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isPresent()) throw UserException.dupliclateUser();

        if (!Objects.equals(password, confirmpasword)) throw UserException.unMatchedPassword();

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public boolean matchedPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

