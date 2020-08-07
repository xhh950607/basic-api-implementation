package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.CommonError;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(RsController.class);

    @GetMapping("/user")
    public ResponseEntity<List<User>> getUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<User> users = userEntities.stream().map(
                entity -> User.builder()
                        .userName(entity.getName())
                        .age(entity.getAge())
                        .gender(entity.getGender())
                        .email(entity.getEmail())
                        .phone(entity.getPhone())
                        .build()
        ).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/user")
    public ResponseEntity addUser(@RequestBody @Valid User user) {
        UserEntity userEntity = UserEntity.builder()
                .name(user.getUserName())
                .age(user.getAge())
                .gender(user.getGender())
                .email(user.getEmail())
                .phone(user.getPhone())
                .voteNum(10)
                .build();
        userRepository.save(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getOne(@PathVariable Integer id) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(id);
        if (optionalUserEntity.isPresent()) {
            UserEntity userEntity = optionalUserEntity.get();
            User user = User.builder()
                    .userName(userEntity.getName())
                    .age(userEntity.getAge())
                    .gender(userEntity.getGender())
                    .email(userEntity.getEmail())
                    .phone(userEntity.getPhone())
                    .build();
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonError> handleException(MethodArgumentNotValidException ex) {
        CommonError err = new CommonError("invalid user");
        logger.error(ex.getClass() + ": invalid user");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }
}
