package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void should_get_user_list() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(jsonPath("$[0].user_name").value("Bob"))
                .andExpect(jsonPath("$[0].user_age").value(20))
                .andExpect(jsonPath("$[0].user_gender").value("male"))
                .andExpect(jsonPath("$[0].user_email").value("234@qq.com"))
                .andExpect(jsonPath("$[0].user_phone").value("12345678902"))
                .andExpect(status().isOk());
    }

    @Test
    void should_add_user() throws Exception {
        String postBody = "{\"userName\":\"Tom\",\"age\":19,\"gender\":\"male\",\"email\":\"123@qq.com\",\"phone\":\"12345678901\"}";

        mockMvc.perform(post("/user").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertEquals(2, UserController.userList.size());
    }

    @Test
    void should_return_400_invalid_user_when_add_given_invalid_user() throws Exception {
        String postBody = "{\"age\":19,\"gender\":\"male\",\"email\":\"123@qq.com\",\"phone\":\"12345678901\"}";

        mockMvc.perform(post("/user").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid user"));
    }

}