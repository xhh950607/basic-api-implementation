package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/user")
    public ResponseEntity addUser(@RequestBody User user) {
        UserEntity userEntity = UserEntity.builder()
                .name(user.getUserName())
                .age(user.getAge())
                .gender(user.getGender())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
        userRepository.save(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/user/{index}")
    public ResponseEntity<User> getOne(@PathVariable Integer index) {
        UserEntity userEntity = userRepository.getUserById(index);
        User user = User.builder()
                .userName(userEntity.getName())
                .age(userEntity.getAge())
                .gender(userEntity.getGender())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .build();
        return ResponseEntity.ok(user);
    }

}
