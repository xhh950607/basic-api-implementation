package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class RsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        RsController.resetRsList();
        UserController.resetUserList();
    }

    @Test
    void should_get_one_rs_given_index() throws Exception {
        mockMvc.perform(get("/rs/1"))
                .andExpect(jsonPath("$.eventName").value("第二条事件"))
                .andExpect(jsonPath("$.keyword").value("关键词2"))
                .andExpect(jsonPath("$.user").doesNotHaveJsonPath())
                .andExpect(status().isOk());
    }

    @Test
    void should_return_400_invalid_index_when_get_one_given_out_bound_index() throws Exception {
        mockMvc.perform(get("/rs/3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid index"));
    }

    @Test
    void should_get_rs_list() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("第一条事件"))
                .andExpect(jsonPath("$[0].keyword").value("关键词1"))
                .andExpect(jsonPath("$[0].user").doesNotHaveJsonPath())
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词2"))
                .andExpect(jsonPath("$[1].user").doesNotHaveJsonPath())
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyword").value("关键词3"))
                .andExpect(jsonPath("$[2].user").doesNotHaveJsonPath())
                .andExpect(status().isOk());
    }

    @Test
    void should_get_rs_list_given_start_and_end() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=3"))
                .andExpect(jsonPath("$[0].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[0].keyword").value("关键词2"))
                .andExpect(jsonPath("$[0].user").doesNotHaveJsonPath())
                .andExpect(jsonPath("$[1].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词3"))
                .andExpect(jsonPath("$[1].user").doesNotHaveJsonPath())
                .andExpect(status().isOk());
    }

    @Test
    void should_return_400_invalid_request_param_when_get_list_given_out_bound_start_or_end() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=4"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid request param"));
    }

    @Test
    void should_only_add_rs_when_add_given_registered_user() throws Exception {
        String postBody = "{\"eventName\":\"第四条事件\",\"keyword\":\"关键词4\",\"user\":{\"userName\":\"Bob\",\"age\":20,\"gender\":\"male\",\"email\":\"234@qq.com\",\"phone\":\"12345678902\"}}";

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("index", "3"));
        Assertions.assertEquals(4, RsController.rsList.size());
        Assertions.assertEquals(1, UserController.userList.size());
    }

    @Test
    void should_add_rs_and_add_user_when_add_given_not_registered_user() throws Exception {
        String postBody = "{\"eventName\":\"第四条事件\",\"keyword\":\"关键词4\",\"user\":{\"userName\":\"Tom\",\"age\":19,\"gender\":\"male\",\"email\":\"123@qq.com\",\"phone\":\"12345678901\"}}";

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("index", "3"));
        Assertions.assertEquals(4, RsController.rsList.size());
        Assertions.assertEquals(2, UserController.userList.size());
    }

    @Test
    void should_return_400_invalid_param_when_add_given_invalid_rs() throws Exception {
        RsEvent rsEvent = new RsEvent("新增事件", "关键词", null);
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid param"));
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
        RsEvent rsEvent = new RsEvent("新增事件", null, UserController.userList.get(0));
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_bad_request_when_add_given_null_event_name() throws Exception {
        RsEvent rsEvent = new RsEvent(null, "关键字", UserController.userList.get(0));
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
    void should_bad_request_when_add_given_null_gender() throws Exception {
        RsEvent rsEvent = new RsEvent("新增事件", "关键字",
                new User("Tom", 13, null, "123@qq.com", "12345678901"));
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_bad_request_when_add_given_age_more_than_100() throws Exception {
        RsEvent rsEvent = new RsEvent("新增事件", "关键字",
                new User("Tom", 101, "male", "123@qq.com", "12345678901"));
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_bad_request_when_add_given_age_less_than_18() throws Exception {
        RsEvent rsEvent = new RsEvent("新增事件", "关键字",
                new User("Tom", 17, "male", "123@qq.com", "12345678901"));
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_bad_request_when_add_given_invalid_email() throws Exception {
        RsEvent rsEvent = new RsEvent("新增事件", "关键字",
                new User("Tom", 19, "male", "123", "12345678901"));
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_bad_request_when_add_given_invalid_phone() throws Exception {
        RsEvent rsEvent = new RsEvent("新增事件", "关键字",
                new User("Tom", 19, "male", "123@qq.com", "1234567"));
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