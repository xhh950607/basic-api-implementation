package com.thoughtworks.rslist.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    private Integer id;

    @Min(0)
    private Integer voteNum;

    @NotNull
    private Integer userId;

    @NotNull
    private String voteTime;

    public Vote(@Min(0) Integer voteNum, @NotNull Integer userId, @NotNull String voteTime) {
        this.voteNum = voteNum;
        this.userId = userId;
        this.voteTime = voteTime;
    }
}
