package com.testmongo.demo.collection;

import lombok.Data;
import lombok.Generated;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("userdata")
@Data
public class User {

    @Id
    @Generated
    private String id;

    private String username;
    private String password;


}
