package com.testmongo.demo.api;

import com.testmongo.demo.model.LoginReq;
import com.testmongo.demo.model.LoginRes;
import com.testmongo.demo.repository.UserRepository;
import com.testmongo.demo.business.UserBusiness;
import com.testmongo.demo.exception.BaseException;
import com.testmongo.demo.model.RegisterReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserApi {

    private final UserBusiness userBusiness;

    public UserApi(UserRepository userRepository, UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> create(@RequestBody RegisterReq req) throws BaseException {
        boolean regResult = userBusiness.create(req);
        return ResponseEntity.ok(regResult);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@RequestBody LoginReq req) throws BaseException {
        LoginRes response = userBusiness.login(req);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test");
    }
}
