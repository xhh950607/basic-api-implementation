package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.entity.RsEventEntitiy;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class RsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RsEventRepository rsRepository;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private UserEntity userEntity;
    private List<RsEventEntitiy> rsEventEntitiys;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .name("Tom")
                .age(20)
                .gender("male")
                .email("123@qq.com")
                .phone("12345678901")
                .build();
        userEntity = userRepository.save(userEntity);

        rsEventEntitiys = Arrays.stream(new int[]{1, 2, 3})
                .mapToObj(i -> RsEventEntitiy.builder()
                        .eventName("event name " + i)
                        .keyword("keyword " + i)
                        .userId(userEntity.getId())
                        .build())
                .map(e -> rsRepository.save(e))
                .collect(Collectors.toList());
    }

    @Test
    void should_get_one_rs_given_index() throws Exception {
        RsEventEntitiy rsEventEntitiy = rsEventEntitiys.get(0);
        mockMvc.perform(get("/rs/" + rsEventEntitiy.getId()))
                .andExpect(jsonPath("$.eventName").value(rsEventEntitiy.getEventName()))
                .andExpect(jsonPath("$.keyword").value(rsEventEntitiy.getKeyword()))
                .andExpect(jsonPath("$.userId").value(rsEventEntitiy.getUserId()))
                .andExpect(status().isOk());
    }

    @Test
    void should_return_400_invalid_id_when_get_one_given_invalid_id() throws Exception {
        mockMvc.perform(get("/rs/" + 100))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid id"));
    }

    //    @Test
//    void should_get_rs_list() throws Exception {
//        mockMvc.perform(get("/rs/list"))
//                .andExpect(jsonPath("$[0].eventName").value(rsEventEntitiys.get(0).getEventName()))
//                .andExpect(jsonPath("$[0].keyword").value(rsEventEntitiys.get(0).getKeyword()))
//                .andExpect(jsonPath("$[0].userId").value(rsEventEntitiys.get(0).getUserId()))
//                .andExpect(jsonPath("$[1].eventName").value(rsEventEntitiys.get(1).getEventName()))
//                .andExpect(jsonPath("$[1].keyword").value(rsEventEntitiys.get(1).getKeyword()))
//                .andExpect(jsonPath("$[1].userId").value(rsEventEntitiys.get(1).getUserId()))
//                .andExpect(jsonPath("$[2].eventName").value(rsEventEntitiys.get(2).getEventName()))
//                .andExpect(jsonPath("$[2].keyword").value(rsEventEntitiys.get(2).getKeyword()))
//                .andExpect(jsonPath("$[2].userId").value(rsEventEntitiys.get(2).getUserId()))
//                .andExpect(status().isOk());
//    }
//
    @Test
    void should_only_add_rs_when_add_given_registered_user() throws Exception {
        RsEvent rsEvent = new RsEvent("trend 4", "keyword 4", userEntity.getId());
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        RsEventEntitiy rsEventEntitiy = rsRepository.findById(4).get();
        assertEquals(rsEvent.getEventName(), rsEventEntitiy.getEventName());
        assertEquals(rsEvent.getKeyword(), rsEventEntitiy.getKeyword());
        assertEquals(rsEvent.getUserId(), rsEventEntitiy.getUserId());
    }

    @Test
    void should_return_400_when_add_given_not_registered_user() throws Exception {
        RsEvent rsEvent = new RsEvent("trend 4", "keyword 4", userEntity.getId());
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
//
//    @Test
//    void should_return_400_invalid_param_when_add_given_invalid_rs() throws Exception {
//        RsEvent rsEvent = new RsEvent("新增事件", "关键词", null);
//        String postBody = objectMapper.writeValueAsString(rsEvent);
//
//        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("invalid param"));
//    }
//
//    @Test
//    void should_bad_request_when_add_given_null_user() throws Exception {
//        RsEvent rsEvent = new RsEvent("新增事件", "关键词", null);
//        String postBody = objectMapper.writeValueAsString(rsEvent);
//
//        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void should_bad_request_when_add_given_null_keyword() throws Exception {
//        RsEvent rsEvent = new RsEvent("新增事件", null, UserController.userList.get(0));
//        String postBody = objectMapper.writeValueAsString(rsEvent);
//
//        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void should_bad_request_when_add_given_null_event_name() throws Exception {
//        RsEvent rsEvent = new RsEvent(null, "关键字", UserController.userList.get(0));
//        String postBody = objectMapper.writeValueAsString(rsEvent);
//
//        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }_
//
//    @Test
//    void should_update_eventName_and_keyword_when_both_not_null() throws Exception {
//        RsEvent rsEvent = new RsEvent("更新事件", "更新关键词", null);
//        String postBody = objectMapper.writeValueAsString(rsEvent);
//
//        mockMvc.perform(put("/rs/0").content(postBody).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//        mockMvc.perform(get("/rs/0"))
//                .andExpect(jsonPath("$.eventName").value("更新事件"))
//                .andExpect(jsonPath("$.keyword").value("更新关键词"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void should_only_update_eventName_when_keyword_is_null() throws Exception {
//        RsEvent rsEvent = new RsEvent("更新事件", null, null);
//        String postBody = objectMapper.writeValueAsString(rsEvent);
//
//        mockMvc.perform(put("/rs/0").content(postBody).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//        mockMvc.perform(get("/rs/0"))
//                .andExpect(jsonPath("$.eventName").value("更新事件"))
//                .andExpect(jsonPath("$.keyword").value("关键词1"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void should_only_update_keyword_when_eventName_is_null() throws Exception {
//        RsEvent rsEvent = new RsEvent(null, "更新关键词", null);
//        String postBody = objectMapper.writeValueAsString(rsEvent);
//
//        mockMvc.perform(put("/rs/0").content(postBody).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//        mockMvc.perform(get("/rs/0"))
//                .andExpect(jsonPath("$.eventName").value("第一条事件"))
//                .andExpect(jsonPath("$.keyword").value("更新关键词"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void should_success_when_delete_rs_by_index() throws Exception {
//        mockMvc.perform(delete("/rs/0"))
//                .andExpect(status().isOk());
//        mockMvc.perform(get("/rs/list"))
//                .andExpect(jsonPath("$[0].eventName").value("第二条事件"))
//                .andExpect(jsonPath("$[0].keyword").value("关键词2"))
//                .andExpect(jsonPath("$[1].eventName").value("第三条事件"))
//                .andExpect(jsonPath("$[1].keyword").value("关键词3"))
//                .andExpect(status().isOk());
//    }
}