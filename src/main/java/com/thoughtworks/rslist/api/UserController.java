package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.CommonError;
import com.thoughtworks.rslist.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class UserController {
    public static List<User> userList;

    static {
        resetUserList();
    }

    public static void resetUserList() {
        userList = Stream.of(
                new User("Bob", 20, "male", "234@qq.com", "12345678902")
        ).collect(Collectors.toList());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @PostMapping("/user")
    public ResponseEntity<Void> addUser(@RequestBody @Valid User user) {
        userList.add(user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonError> handleException(MethodArgumentNotValidException ex) {
        CommonError err = new CommonError("invalid user");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }
}
