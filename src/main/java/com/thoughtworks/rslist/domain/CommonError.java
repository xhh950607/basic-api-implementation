package com.thoughtworks.rslist.domain;

import lombok.Data;

@Data
public class CommonError {
    private String error;

    public CommonError(String error) {
        this.error = error;
    }
}
