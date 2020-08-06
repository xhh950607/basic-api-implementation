package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.entity.RsEventEntitiy;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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

    @Autowired
    private VoteRepository voteRepository;

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
                .voteNum(10)
                .build();
        userEntity = userRepository.save(userEntity);

        rsEventEntitiys = Arrays.stream(new int[]{1, 2, 3})
                .mapToObj(i -> RsEventEntitiy.builder()
                        .eventName("event name " + i)
                        .keyword("keyword " + i)
                        .userId(userEntity.getId())
                        .voteNum(0)
                        .build())
                .map(e -> rsRepository.save(e))
                .collect(Collectors.toList());
    }

    @AfterEach
    void clearUp() {
        voteRepository.deleteAll();
        userRepository.deleteAll();
        rsRepository.deleteAll();
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

        List<RsEventEntitiy> entitiys = rsRepository.findAll();
        assertEquals(4, entitiys.size());
        RsEventEntitiy rsEventEntitiy = entitiys.get(entitiys.size() - 1);
        assertEquals(rsEvent.getEventName(), rsEventEntitiy.getEventName());
        assertEquals(rsEvent.getKeyword(), rsEventEntitiy.getKeyword());
        assertEquals(rsEvent.getUserId(), rsEventEntitiy.getUserId());
    }

    @Test
    void should_return_400_when_add_given_not_registered_user() throws Exception {
        RsEvent rsEvent = new RsEvent("trend 4", "keyword 4", 100);
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_delete_related_rs_event_when_delete_user_given_userId() throws Exception {
        mockMvc.perform(delete("/user/" + userEntity.getId()))
                .andExpect(status().isOk());

        assertEquals(0, userRepository.findAll().size());
        assertEquals(0, rsRepository.findAll().size());
    }

    @Test
    void should_return_400_invalid_param_when_add_given_null_user_id() throws Exception {
        RsEvent rsEvent = new RsEvent("trend 4", "keyword 4", null);
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid param"));
    }

    @Test
    void should_return_400_invalid_param_when_add_given_null_keyword() throws Exception {
        RsEvent rsEvent = new RsEvent("trend 4", null, userEntity.getId());
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid param"));
    }

    @Test
    void should_return_400_invalid_param_when_add_given_null_event_name() throws Exception {
        RsEvent rsEvent = new RsEvent(null, "keyword 4", userEntity.getId());
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs").content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid param"));
    }

    @Test
    void should_update_eventName_and_keyword_when_both_not_null() throws Exception {
        RsEventEntitiy entitiy = rsEventEntitiys.get(0);
        RsEvent rsEvent = new RsEvent("update trend", "update keyword", entitiy.getUserId());
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(patch("/rs/" + entitiy.getId()).content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/" + entitiy.getId()))
                .andExpect(jsonPath("$.eventName").value(rsEvent.getEventName()))
                .andExpect(jsonPath("$.keyword").value(rsEvent.getKeyword()))
                .andExpect(status().isOk());
    }

    @Test
    void should_only_update_eventName_when_keyword_is_null() throws Exception {
        RsEventEntitiy entitiy = rsEventEntitiys.get(0);
        RsEvent rsEvent = new RsEvent("update trend", null, entitiy.getUserId());
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(patch("/rs/" + entitiy.getId()).content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/" + entitiy.getId()))
                .andExpect(jsonPath("$.eventName").value(rsEvent.getEventName()))
                .andExpect(jsonPath("$.keyword").value(entitiy.getKeyword()))
                .andExpect(status().isOk());
    }

    @Test
    void should_only_update_keyword_when_eventName_is_null() throws Exception {
        RsEventEntitiy entitiy = rsEventEntitiys.get(0);
        RsEvent rsEvent = new RsEvent(null, "update keyword", entitiy.getUserId());
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(patch("/rs/" + entitiy.getId()).content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/" + entitiy.getId()))
                .andExpect(jsonPath("$.eventName").value(entitiy.getEventName()))
                .andExpect(jsonPath("$.keyword").value(rsEvent.getKeyword()))
                .andExpect(status().isOk());
    }

    @Test
    void should_return_400_when_update_given_error_userId() throws Exception {
        RsEventEntitiy entitiy = rsEventEntitiys.get(0);
        RsEvent rsEvent = new RsEvent("update trend", "update keyword", 1000);
        String postBody = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(patch("/rs/" + entitiy.getId()).content(postBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_related_vote_when__rs_by_id() throws Exception {
        RsEventEntitiy rsEventEntitiy = rsEventEntitiys.get(0);

        mockMvc.perform(delete("/rs/" + rsEventEntitiy.getId()))
                .andExpect(status().isOk());

        assertEquals(false, rsRepository.findById(rsEventEntitiy.getId()).isPresent());
    }

    @Test
    void should_vote_success() throws Exception {
        LocalDateTime time = LocalDateTime.now();
        Vote vote = new Vote(5, userEntity.getId(), time.toString());
        String postStr = objectMapper.writeValueAsString(vote);

        RsEventEntitiy rsEventEntitiy = rsEventEntitiys.get(0);
        Integer oldUserVoteNum = userEntity.getVoteNum();
        Integer oldRsEventVoteNum = rsEventEntitiy.getVoteNum();

        mockMvc.perform(post("/rs/vote/" + rsEventEntitiy.getId())
                .content(postStr)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        VoteEntity voteEntity = voteRepository.findAll().get(0);
        assertEquals(5, voteEntity.getVoteNum());
        assertEquals(userEntity.getId(), voteEntity.getUser().getId());
        assertEquals(rsEventEntitiy.getId(), voteEntity.getRsEvent().getId());

        assertEquals(oldUserVoteNum - 5, userRepository.findById(userEntity.getId()).get().getVoteNum());
        assertEquals(oldRsEventVoteNum + 5, rsRepository.findById(rsEventEntitiy.getId()).get().getVoteNum());
    }

    @Test
    void should_return_400_when_vote_too_much() throws Exception {
        Vote vote = new Vote(15, userEntity.getId(), LocalDateTime.now().toString());
        String postStr = objectMapper.writeValueAsString(vote);

        mockMvc.perform(post("/rs/vote/" + rsEventEntitiys.get(0).getId())
                .content(postStr)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}