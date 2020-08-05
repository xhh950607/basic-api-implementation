package com.thoughtworks.rslist.domain;

public class RsEvent {
    private String eventName;
    private String keyword;
    private User user;

    public RsEvent(String eventName, String keyword, User user) {
        this.eventName = eventName;
        this.keyword = keyword;
        this.user = user;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
