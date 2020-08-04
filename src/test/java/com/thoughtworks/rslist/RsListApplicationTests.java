package com.thoughtworks.rslist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class RsListApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void should_get_one_rs_given_index() throws Exception {
        mockMvc.perform(get("/rs/1"))
                .andExpect(jsonPath("$.eventName").value("第一条事件"))
                .andExpect(jsonPath("$.keyword").value("关键词1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/2"))
                .andExpect(jsonPath("$.eventName").value("第二条事件"))
                .andExpect(jsonPath("$.keyword").value("关键词2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/3"))
                .andExpect(jsonPath("$.eventName").value("第三条事件"))
                .andExpect(jsonPath("$.keyword").value("关键词3"))
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
        mockMvc.perform(get("/rs/list?start=1&end=2"))
                .andExpect(jsonPath("$[0].eventName").value("第一条事件"))
                .andExpect(jsonPath("$[0].keyword").value("关键词1"))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list?start=1&end=3"))
                .andExpect(jsonPath("$[0].eventName").value("第一条事件"))
                .andExpect(jsonPath("$[0].keyword").value("关键词1"))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词2"))
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyword").value("关键词3"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list?start=2&end=3"))
                .andExpect(jsonPath("$[0].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[0].keyword").value("关键词2"))
                .andExpect(jsonPath("$[1].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词3"))
                .andExpect(status().isOk());
    }

    @Test
    void should_success_when_add_rs() throws Exception {
        RsEvent rsEvent = new RsEvent("第四条事件", "关键词4");
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("第一条事件"))
                .andExpect(jsonPath("$[0].keyword").value("关键词1"))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词2"))
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyword").value("关键词3"))
                .andExpect(jsonPath("$[3].eventName").value("第四条事件"))
                .andExpect(jsonPath("$[3].keyword").value("关键词4"))
                .andExpect(status().isOk());
    }

    @Test
    void should_update_eventName_and_keyword_when_both_not_null() throws Exception {
        RsEvent rsEvent = new RsEvent("更新事件", "更新关键词");
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(put("/rs/event?index=1").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("更新事件"))
                .andExpect(jsonPath("$[0].keyword").value("更新关键词"))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词2"))
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyword").value("关键词3"))
                .andExpect(status().isOk());
    }

    @Test
    void should_only_update_eventName_when_keyword_is_null() throws Exception {
        RsEvent rsEvent = new RsEvent("更新事件", null);
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(put("/rs/event?index=1").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("更新事件"))
                .andExpect(jsonPath("$[0].keyword").value("关键词1"))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词2"))
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyword").value("关键词3"))
                .andExpect(status().isOk());
    }

    @Test
    void should_only_update_keyword_when_eventName_is_null() throws Exception {
        RsEvent rsEvent = new RsEvent(null, "更新关键词");
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(put("/rs/event?index=1").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("第一条事件"))
                .andExpect(jsonPath("$[0].keyword").value("更新关键词"))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词2"))
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyword").value("关键词3"))
                .andExpect(status().isOk());
    }

    @Test
    void should_success_when_delete_rs_by_index() throws Exception {
        mockMvc.perform(delete("/rs/event?index=1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[0].keyword").value("关键词2"))
                .andExpect(jsonPath("$[1].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[1].keyword").value("关键词3"))
                .andExpect(status().isOk());
    }
}
