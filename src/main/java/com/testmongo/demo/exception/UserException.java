package com.testmongo.demo.exception;

public class UserException extends BaseException {
    public UserException(String code) {
        super("user." + code);
    }

    public static UserException dupliclateUser() {
        return new UserException("username.already.use");
    }

    public static UserException unMatchedPassword() {
        return new UserException("password.unmatched");
    }

    public static UserException loginError() {
        return new UserException("login.error");
    }
}
