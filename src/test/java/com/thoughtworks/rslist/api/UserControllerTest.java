package com.thoughtworks.rslist.api;

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

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    void should_get_user_list() throws Exception {
        UserEntity userEntity = saveOneUser();
        mockMvc.perform(get("/user"))
                .andExpect(jsonPath("$[0].user_name").value(userEntity.getName()))
                .andExpect(jsonPath("$[0].user_age").value(userEntity.getAge()))
                .andExpect(jsonPath("$[0].user_gender").value(userEntity.getGender()))
                .andExpect(jsonPath("$[0].user_email").value(userEntity.getEmail()))
                .andExpect(jsonPath("$[0].user_phone").value(userEntity.getPhone()))
                .andExpect(status().isOk());
    }

    @Test
    void should_get_one_given_id() throws Exception {
        UserEntity userEntity = saveOneUser();

        mockMvc.perform(get("/user/" + userEntity.getId()))
                .andExpect(jsonPath("user_name").value(userEntity.getName()))
                .andExpect(jsonPath("user_age").value(userEntity.getAge()))
                .andExpect(jsonPath("user_gender").value(userEntity.getGender()))
                .andExpect(jsonPath("user_email").value(userEntity.getEmail()))
                .andExpect(jsonPath("user_phone").value(userEntity.getPhone()))
                .andExpect(status().isOk());
    }

    @Test
    void should_save_user_in_db_when_register_user() throws Exception {
        String postBody = "{\"userName\":\"Tom\",\"age\":19,\"gender\":\"male\",\"email\":\"123@qq.com\",\"phone\":\"12345678901\"}";

        mockMvc.perform(post("/user").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<UserEntity> userEntities = userRepository.findAll();
        UserEntity actualUser = userEntities.get(0);
        assertEquals(1, userEntities.size());
        assertEquals("Tom", actualUser.getName());
        assertEquals(19, actualUser.getAge());
        assertEquals("male", actualUser.getGender());
        assertEquals("123@qq.com", actualUser.getEmail());
        assertEquals("12345678901", actualUser.getPhone());
    }

    @Test
    void should_return_400_invalid_user_when_add_given_invalid_user() throws Exception {
        String postBody = "{\"age\":19,\"gender\":\"male\",\"email\":\"123@qq.com\",\"phone\":\"12345678901\"}";

        mockMvc.perform(post("/user").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid user"));
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