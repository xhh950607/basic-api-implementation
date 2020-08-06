package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.CommonError;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.InvalidIndexException;
import com.thoughtworks.rslist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class RsController {
    public static List<RsEvent> rsList;

    @Autowired
    UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(RsController.class);

    static {
        resetRsList();
    }

    public static void resetRsList() {
        rsList = Stream.of(
                new RsEvent("第一条事件", "关键词1", null),
                new RsEvent("第二条事件", "关键词2", null),
                new RsEvent("第三条事件", "关键词3", null)
        ).collect(Collectors.toList());
    }

    @GetMapping("/rs/{index}")
    public ResponseEntity<RsEvent> getOneRsByIndex(@PathVariable int index) throws InvalidIndexException {
        if (index < 0 || index >= rsList.size())
            throw new InvalidIndexException("invalid index");
        RsEvent rs = rsList.get(index);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @GetMapping("/rs/list")
    public ResponseEntity<List<RsEvent>> getRsListBetween(@RequestParam(required = false) Integer start,
                                                          @RequestParam(required = false) Integer end) throws InvalidIndexException {
        List<RsEvent> res;
        if (start == null || end == null)
            res = rsList;
        else if (start < 0 || end > rsList.size())
            throw new InvalidIndexException("invalid request param");
        else
            res = rsList.subList(start, end);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

//    @PostMapping("/rs")
//    public ResponseEntity<Void> addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
//        User user = rsEvent.getUser();
//        if (!isRegistered(user)) {
//            UserController.userList.add(user);
//        }
//        rsList.add(rsEvent);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .header("index", rsList.indexOf(rsEvent) + "")
//                .body(null);
//    }

    private boolean isRegistered(Integer userId) {
        return userRepository.findById(userId).isPresent();
    }

    @PutMapping("/rs/{index}")
    public ResponseEntity<Void> updateRsEvent(@PathVariable int index, @RequestBody RsEvent newRsEvent) {
        RsEvent oldRsEvent = rsList.get(index);
        String newEventName = newRsEvent.getEventName();
        String newKeyword = newRsEvent.getKeyword();
        if (newEventName != null)
            oldRsEvent.setEventName(newEventName);
        if (newKeyword != null)
            oldRsEvent.setKeyword(newKeyword);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("/rs/{index}")
    public ResponseEntity<Void> deleteRsEvent(@PathVariable int index) {
        rsList.remove(index);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @ExceptionHandler({InvalidIndexException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<CommonError> handleException(Exception ex) {
        String errMessage;
        if (ex instanceof InvalidIndexException)
            errMessage = ex.getMessage();
        else
            errMessage = "invalid param";
        CommonError err = new CommonError(errMessage);
        logger.error(ex.getClass() + ":" + errMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

}
