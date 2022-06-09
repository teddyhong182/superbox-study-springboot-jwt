package com.superbox.study.service;

import com.superbox.study.entity.MemberToken;
import com.superbox.study.repository.MemberTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final MemberTokenRepository memberTokenRepository;

    public Optional<MemberToken> findByToken(String refreshToken) {
        return memberTokenRepository.findByToken(refreshToken);
    }

    public MemberToken verifyExpiration(MemberToken token) {
        // 만료시 삭제
        if (token.getExpireAt().compareTo(LocalDateTime.now()) < 0) {
            memberTokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Failed for [%s]: Refresh token was expired. Please make a new signin request", token.getToken()));
        }
        return token;
    }

    public MemberToken createRefreshToken(Long id) {
//        refreshToken.setToken(UUID.randomUUID().toString());

        return null;
    }
}
