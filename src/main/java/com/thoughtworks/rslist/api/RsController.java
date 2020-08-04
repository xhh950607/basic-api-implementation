package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class RsController {
    private static List<RsEvent> rsList = Stream.of(
            new RsEvent("第一条事件", "关键词1"),
            new RsEvent("第二条事件", "关键词2"),
            new RsEvent("第三条事件", "关键词3")).collect(Collectors.toList());

    @GetMapping("/rs/{index}")
    public RsEvent getOneRsByIndex(@PathVariable int index) {
        return rsList.get(index - 1);
    }

    @GetMapping("/rs/list")
    public List<RsEvent> getRsListBetween(@RequestParam(required = false) Integer start,
                                          @RequestParam(required = false) Integer end) {
        if (start == null || end == null)
            return rsList;
        return rsList.subList(start - 1, end);
    }

    @PostMapping("/rs/event")
    public void addRsEvent(@RequestBody RsEvent rsEvent) {
        rsList.add(rsEvent);
    }

    @PutMapping("/rs/event")
    public void updateRsEvent(@RequestParam int index, @RequestBody RsEvent newRsEvent) {
        RsEvent oldRsEvent = rsList.get(index - 1);
        String newEventName = newRsEvent.getEventName();
        String newKeyword = newRsEvent.getKeyword();
        if (newEventName != null)
            oldRsEvent.setEventName(newEventName);
        if (newKeyword != null)
            oldRsEvent.setKeyword(newKeyword);
    }

    @DeleteMapping("/rs/event")
    public void deleteRsEvent(@RequestParam int index) {
        rsList.remove(index - 1);
    }

}
