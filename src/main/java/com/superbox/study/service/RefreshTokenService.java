package com.superbox.study.service;

import com.superbox.study.config.exception.TokenRefreshException;
import com.superbox.study.entity.MemberToken;
import com.superbox.study.repository.MemberTokenRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.ordering.antlr.ColumnMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}
