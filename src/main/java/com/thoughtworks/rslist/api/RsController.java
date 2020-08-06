package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.CommonError;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.entity.RsEventEntitiy;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.exception.InvalidIndexException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
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
    @Autowired
    VoteRepository voteRepository;

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
                    .voteNum(0)
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

    @Transactional
    @PostMapping("/rs/vote/{rsEventId}")
    public ResponseEntity vote(@PathVariable Integer rsEventId, @RequestBody @Valid Vote vote) {
        Optional<RsEventEntitiy> rsEventEntitiyOptional = rsEventRepository.findById(rsEventId);
        if (!rsEventEntitiyOptional.isPresent())
            return ResponseEntity.badRequest().build();
        RsEventEntitiy rsEventEntitiy = rsEventEntitiyOptional.get();

        Optional<UserEntity> userEntityOptional = userRepository.findById(vote.getUserId());
        if (!userEntityOptional.isPresent())
            return ResponseEntity.badRequest().build();
        UserEntity userEntity = userEntityOptional.get();

        VoteEntity voteEntity = VoteEntity.builder()
                .voteNum(vote.getVoteNum())
                .voteTime(LocalDateTime.parse(vote.getVoteTime()))
                .rsEventId(rsEventEntitiy.getId())
                .userId(userEntity.getId())
                .build();

        userEntity.setVoteNum(userEntity.getVoteNum() - vote.getVoteNum());
        rsEventEntitiy.setVoteNum(rsEventEntitiy.getVoteNum() + vote.getVoteNum());
        voteRepository.save(voteEntity);
        userRepository.save(userEntity);
        rsEventRepository.save(rsEventEntitiy);

        return ResponseEntity.created(null).build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<CommonError> handleException(Exception ex) {
        CommonError err = new CommonError("invalid param");
        logger.error(ex.getClass() + ": invalid param");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

}
