package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class RsEvent {
    @NotNull
    private String eventName;

    @NotNull
    private String keyword;

    @NotNull
    @Valid
    private User user;

    public RsEvent(String eventName, String keyword, User user) {
        this.eventName = eventName;
        this.keyword = keyword;
        this.user = user;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonProperty
    public void setUser(User user) {
        this.user = user;
    }
}
