package com.testmongo.demo.model;

import lombok.Data;

@Data
public class RegisterReq {

    private String username;

    private String password;

    private String confirmpassword;

}
