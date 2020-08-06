package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @NotNull
    @Size(max = 8)
    private String userName;

    @Max(100)
    @Min(18)
    private int age;

    @NotNull
    private String gender;

    @Email
    private String email;

    @Pattern(regexp = "1\\d{10}")
    private String phone;

    @JsonProperty("user_name")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("user_age")
    public int getAge() {
        return age;
    }

    @JsonProperty("age")
    public void setAge(int age) {
        this.age = age;
    }

    @JsonProperty("user_gender")
    public String getGender() {
        return gender;
    }

    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonProperty("user_email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("user_phone")
    public String getPhone() {
        return phone;
    }

    @JsonProperty("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }
}