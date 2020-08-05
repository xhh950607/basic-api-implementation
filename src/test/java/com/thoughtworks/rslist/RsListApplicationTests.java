package com.thoughtworks.rslist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.api.RsController;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class RsListApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        RsController.resetData();
    }

    @Test
    void should_get_one_rs_given_index() throws Exception {
        mockMvc.perform(get("/rs/1"))
                .andExpect(jsonPath("$.eventName").value("第二条事件"))
                .andExpect(jsonPath("$.keyword").value("关键词2"))
                .andExpect(status().isOk());
    }

    @Test
    void should_get_rs_list() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("第一条事件"))
                .andExpect(jsonPath("$[0].keyword").value("关键词1"))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词2"))
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyword").value("关键词3"))
                .andExpect(status().isOk());
    }

    @Test
    void should_get_rs_list_given_start_and_end() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=3"))
                .andExpect(jsonPath("$[0].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[0].keyword").value("关键词2"))
                .andExpect(jsonPath("$[1].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词3"))
                .andExpect(status().isOk());
    }

    @Test
    void should_only_add_rs_when_add_given_registered_user() throws Exception {
        RsEvent rsEvent = new RsEvent("第四条事件", "关键词4", RsController.userList.get(0));
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertEquals(4, RsController.rsList.size());
        Assertions.assertEquals(1, RsController.userList.size());
    }

    @Test
    void should_add_rs_and_add_user_when_add_given_not_registered_user() throws Exception {
        RsEvent rsEvent = new RsEvent("第四条事件", "关键词4",
                new User("Tom", 13, "male", "123@qq.com", "12345678901"));
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertEquals(4, RsController.rsList.size());
        Assertions.assertEquals(2, RsController.userList.size());
    }

    @Test
    void should_bad_request_when_add_given_null_user() throws Exception {
        RsEvent rsEvent = new RsEvent("新增事件", "关键词", null);
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_bad_request_when_add_given_null_keyword() throws Exception {
        RsEvent rsEvent = new RsEvent("新增事件", null, RsController.userList.get(0));
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_bad_request_when_add_given_null_event_name() throws Exception {
        RsEvent rsEvent = new RsEvent(null, "关键字", RsController.userList.get(0));
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_bad_request_when_add_given_null_user_name() throws Exception {
        RsEvent rsEvent = new RsEvent("新增事件", "关键字",
                new User(null, 13, "male", "123@qq.com", "12345678901"));
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_bad_request_when_add_given_user_name_exceed() throws Exception {
        RsEvent rsEvent = new RsEvent("新增事件", "关键字",
                new User("Tom123456", 13, "male", "123@qq.com", "12345678901"));
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_update_eventName_and_keyword_when_both_not_null() throws Exception {
        RsEvent rsEvent = new RsEvent("更新事件", "更新关键词", null);
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(put("/rs/0").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/0"))
                .andExpect(jsonPath("$.eventName").value("更新事件"))
                .andExpect(jsonPath("$.keyword").value("更新关键词"))
                .andExpect(status().isOk());
    }

    @Test
    void should_only_update_eventName_when_keyword_is_null() throws Exception {
        RsEvent rsEvent = new RsEvent("更新事件", null, null);
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(put("/rs/0").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/0"))
                .andExpect(jsonPath("$.eventName").value("更新事件"))
                .andExpect(jsonPath("$.keyword").value("关键词1"))
                .andExpect(status().isOk());
    }

    @Test
    void should_only_update_keyword_when_eventName_is_null() throws Exception {
        RsEvent rsEvent = new RsEvent(null, "更新关键词", null);
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(put("/rs/0").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/0"))
                .andExpect(jsonPath("$.eventName").value("第一条事件"))
                .andExpect(jsonPath("$.keyword").value("更新关键词"))
                .andExpect(status().isOk());
    }

    @Test
    void should_success_when_delete_rs_by_index() throws Exception {
        mockMvc.perform(delete("/rs/0"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[0].keyword").value("关键词2"))
                .andExpect(jsonPath("$[1].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词3"))
                .andExpect(status().isOk());
    }
}
