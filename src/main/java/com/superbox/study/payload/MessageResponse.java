package com.superbox.study.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {

    private String message;
    public MessageResponse(String s) {
        this.message = s;
    }
}
