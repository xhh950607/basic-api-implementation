package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

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
}