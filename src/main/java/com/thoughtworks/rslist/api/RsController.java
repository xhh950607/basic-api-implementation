package com.thoughtworks.rslist.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class RsController {
    private List<String> rsList = Arrays.asList("第一条事件", "第二条事件", "第三条事件");

    @GetMapping("/rs/{index}")
    public String getOneRsByIndex(@PathVariable int index) {
        return rsList.get(index - 1);
    }

    @GetMapping("/rs/list")
    public String getRsListBetween(@RequestParam int start,
                                   @RequestParam int end) {
        return rsList.subList(start - 1, end).toString();
    }
}
