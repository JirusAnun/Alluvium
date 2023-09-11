package com.testmongo.demo.business;

import com.testmongo.demo.collection.User;
import com.testmongo.demo.exception.BaseException;
import com.testmongo.demo.exception.UserException;
import com.testmongo.demo.model.LoginReq;
import com.testmongo.demo.model.LoginRes;
import com.testmongo.demo.model.RegisterReq;
import com.testmongo.demo.service.TokenService;
import com.testmongo.demo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserBusiness {

    private final UserService userService;

    private final TokenService tokenService;

    public UserBusiness(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    public Boolean create(RegisterReq req) throws BaseException {
        userService.create(req.getUsername(), req.getPassword(), req.getConfirmpassword());
        return true;
    }

    public LoginRes login(LoginReq req) throws BaseException {

        Optional<User> opt = userService.findbyUsername(req.getUsername());
        if (opt.isEmpty()) throw UserException.loginError();

        User user = opt.get();
        if (!userService.matchedPassword(req.getPassword(), user.getPassword())) throw UserException.loginError();

        LoginRes response = new LoginRes();
        response.setToken(tokenService.tokenize(user));
        return response;
    }

}
