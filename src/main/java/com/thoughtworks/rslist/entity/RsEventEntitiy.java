package com.thoughtworks.rslist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "rs_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsEventEntitiy {

    @Id
    @GeneratedValue
    private Integer id;

    private String eventName;

    private String keyword;

    private Integer voteNum;

    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", insertable = false, updatable = false)
    private UserEntity user;
}
