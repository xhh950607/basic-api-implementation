package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    void should_save_user_in_db_when_register_user() throws Exception {
        User user = new User("Tom", 20, "male", "123@qq.com", "12345678901");
        String postBody = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<UserEntity> userEntities = userRepository.findAll();
        UserEntity actualUser = userEntities.get(0);
        assertEquals(1, userEntities.size());
        assertEquals(user.getUserName(), actualUser.getName());
        assertEquals(user.getAge(), actualUser.getAge());
        assertEquals(user.getGender(), actualUser.getGender());
        assertEquals(user.getEmail(), actualUser.getEmail());
        assertEquals(user.getPhone(), actualUser.getPhone());
    }

    @Test
    void should_get_one_given_id() throws Exception {
        UserEntity userEntity = saveOneUser();

        mockMvc.perform(get("/user/" + userEntity.getId()))
                .andExpect(jsonPath("userName").value(userEntity.getName()))
                .andExpect(jsonPath("age").value(userEntity.getAge()))
                .andExpect(jsonPath("gender").value(userEntity.getGender()))
                .andExpect(jsonPath("email").value(userEntity.getEmail()))
                .andExpect(jsonPath("phone").value(userEntity.getPhone()))
                .andExpect(status().isOk());
    }

    @Test
    void should_delete_user_given_id() throws Exception {
        UserEntity userEntity = saveOneUser();

        mockMvc.perform(delete("/user/" + userEntity.getId()))
                .andExpect(status().isOk());

        assertEquals(false, userRepository.findById(userEntity.getId()).isPresent());
    }

    private UserEntity saveOneUser() {
        UserEntity userEntity = UserEntity.builder()
                .name("Tom")
                .age(20)
                .gender("male")
                .email("123@qq.com")
                .phone("12345678901")
                .build();
        return userRepository.save(userEntity);
    }
}