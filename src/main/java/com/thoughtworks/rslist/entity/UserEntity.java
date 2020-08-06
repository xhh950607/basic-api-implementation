package com.thoughtworks.rslist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;
    private Integer age;
    private String gender;
    private String email;
    private String phone;
    private Integer voteNum;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "userId")
    private List<RsEventEntitiy> rsEventList;
}
