package com.superbox.study.payload;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class JwtResponse {

    private String token;
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

}
