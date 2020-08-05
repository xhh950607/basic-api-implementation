package com.thoughtworks.rslist.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

    private String userName;
    private int age;
    private String gender;
    private String email;
    private String phone;

    public User(String userName, int age, String gender, String email, String phone) {
        this.userName = userName;
        this.age = age;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
    }
}
