package com.thoughtworks.rslist.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

}