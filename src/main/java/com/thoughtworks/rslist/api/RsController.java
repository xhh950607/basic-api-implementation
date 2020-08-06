package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.CommonError;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.RsEventEntitiy;
import com.thoughtworks.rslist.exception.InvalidIndexException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class RsController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;

    Logger logger = LoggerFactory.getLogger(RsController.class);

    @GetMapping("/rs/{id}")
    public ResponseEntity getOneRsById(@PathVariable Integer id) throws InvalidIndexException {
        Optional<RsEventEntitiy> entitiyOptional = rsEventRepository.findById(id);
        if (entitiyOptional.isPresent()) {
            RsEventEntitiy entitiy = entitiyOptional.get();
            RsEvent rsEvent = RsEvent.builder()
                    .eventName(entitiy.getEventName())
                    .keyword(entitiy.getKeyword())
                    .userId(entitiy.getUserId())
                    .build();
            return ResponseEntity.ok(rsEvent);
        } else {
            return ResponseEntity.badRequest().body(new CommonError("invalid id"));
        }
    }

//    @GetMapping("/rs/list")
//    public ResponseEntity<List<RsEvent>> getRsListBetween() throws InvalidIndexException {
//        List<RsEvent> res;
//        if (start == null || end == null)
//            res = rsList;
//        else if (start < 0 || end > rsList.size())
//            throw new InvalidIndexException("invalid request param");
//        else
//            res = rsList.subList(start, end);
//        return ResponseEntity.status(HttpStatus.OK).body(res);
//    }

    @PostMapping("/rs")
    public ResponseEntity addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
        if (isRegistered(rsEvent.getUserId())) {
            RsEventEntitiy entitiy = RsEventEntitiy.builder()
                    .eventName(rsEvent.getEventName())
                    .keyword(rsEvent.getKeyword())
                    .userId(rsEvent.getUserId())
                    .build();
            rsEventRepository.save(entitiy);
            return ResponseEntity.created(null).build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean isRegistered(Integer userId) {
        return userRepository.findById(userId).isPresent();
    }

    @PatchMapping("/rs/{id}")
    public ResponseEntity updateRsEvent(@PathVariable Integer id, @RequestBody RsEvent rsEvent) {
        Optional<RsEventEntitiy> entitiyOptional = rsEventRepository.findById(id);
        if (entitiyOptional.isPresent() && rsEvent.getUserId() != null
                && entitiyOptional.get().getUserId().equals(rsEvent.getUserId())) {
            RsEventEntitiy entitiy = entitiyOptional.get();
            if (!StringUtils.isEmpty(rsEvent.getEventName())) {
                entitiy.setEventName(rsEvent.getEventName());
            }
            if (!StringUtils.isEmpty(rsEvent.getKeyword())) {
                entitiy.setKeyword(rsEvent.getKeyword());
            }
            rsEventRepository.save(entitiy);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
//
//    @DeleteMapping("/rs/{index}")
//    public ResponseEntity<Void> deleteRsEvent(@PathVariable int index) {
//        rsList.remove(index);
//        return ResponseEntity.status(HttpStatus.OK).body(null);
//    }
//
//    @ExceptionHandler({InvalidIndexException.class, MethodArgumentNotValidException.class})
//    public ResponseEntity<CommonError> handleException(Exception ex) {
//        String errMessage;
//        if (ex instanceof InvalidIndexException)
//            errMessage = ex.getMessage();
//        else
//            errMessage = "invalid param";
//        CommonError err = new CommonError(errMessage);
//        logger.error(ex.getClass() + ":" + errMessage);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
//    }

}
