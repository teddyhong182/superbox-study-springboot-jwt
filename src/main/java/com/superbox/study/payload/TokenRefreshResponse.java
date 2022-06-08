package com.superbox.study.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
public class TokenRefreshResponse {

    private String accessToken;
    private String refreshToken;




}
