package com.thoughtworks.rslist.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class User {

    @NotNull
    @Size(max = 8)
    private String userName;
    @Max(100)
    @Min(18)
    private int age;
    @NotNull
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
