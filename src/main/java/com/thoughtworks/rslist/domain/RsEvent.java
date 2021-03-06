package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsEvent {

    private Integer id;
    private Integer voteNum;

    @NotNull
    private String eventName;

    @NotNull
    private String keyword;

    @NotNull
    private Integer userId;

    public RsEvent(@NotNull String eventName, @NotNull String keyword, @NotNull Integer userId) {
        this.eventName = eventName;
        this.keyword = keyword;
        this.userId = userId;
    }
}
