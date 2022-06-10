package com.superbox.study.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class JwtResponse {

    private String token;
    private String refreshToken;

    private Long id;
    private String username;
    private String email;
    private List<String> roles;

}
