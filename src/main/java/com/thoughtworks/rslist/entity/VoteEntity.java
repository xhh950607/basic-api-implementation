package com.thoughtworks.rslist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteEntity {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer voteNum;

    private LocalDateTime voteTime;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "rs_event_id")
    private Integer rsEventId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "rs_event_id", insertable = false, updatable = false)
    private RsEventEntitiy rsEvent;
}
