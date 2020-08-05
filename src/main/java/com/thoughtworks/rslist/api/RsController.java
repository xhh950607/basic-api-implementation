package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class RsController {
    public static List<RsEvent> rsList;
    public static List<User> userList;

    static {
        resetData();
    }

    public static void resetData() {
        resetRsList();
        resetUserList();
    }

    private static void resetRsList() {
        rsList = Stream.of(
                new RsEvent("第一条事件", "关键词1", null),
                new RsEvent("第二条事件", "关键词2", null),
                new RsEvent("第三条事件", "关键词3", null)
        ).collect(Collectors.toList());
    }

    private static void resetUserList() {
        userList = Stream.of(
                new User("Bob", 20, "male", "234@qq.com", "12345678902")
        ).collect(Collectors.toList());
    }

    @GetMapping("/rs/{index}")
    public ResponseEntity<RsEvent> getOneRsByIndex(@PathVariable int index) {
        RsEvent rs = rsList.get(index);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @GetMapping("/rs/list")
    public List<RsEvent> getRsListBetween(@RequestParam(required = false) Integer start,
                                          @RequestParam(required = false) Integer end) {
        if (start == null || end == null)
            return rsList;
        return rsList.subList(start, end);
    }

    @PostMapping("/rs")
    public void addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
        User user = rsEvent.getUser();
        if (!isRegistered(user)) {
            userList.add(user);
        }
        rsList.add(rsEvent);
    }

    private boolean isRegistered(User user) {
        return userList.stream()
                .anyMatch(u -> u.getUserName().equals(user.getUserName()));
    }

    @PutMapping("/rs/{index}")
    public void updateRsEvent(@PathVariable int index, @RequestBody RsEvent newRsEvent) {
        RsEvent oldRsEvent = rsList.get(index);
        String newEventName = newRsEvent.getEventName();
        String newKeyword = newRsEvent.getKeyword();
        if (newEventName != null)
            oldRsEvent.setEventName(newEventName);
        if (newKeyword != null)
            oldRsEvent.setKeyword(newKeyword);
    }

    @DeleteMapping("/rs/{index}")
    public void deleteRsEvent(@PathVariable int index) {
        rsList.remove(index);
    }

}
