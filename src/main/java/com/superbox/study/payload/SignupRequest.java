package com.superbox.study.payload;

import com.superbox.study.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    private String username;
    private String password;
    private String email;
}
