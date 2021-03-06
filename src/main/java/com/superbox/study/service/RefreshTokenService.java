package com.superbox.study.service;

import com.superbox.study.entity.Member;
import com.superbox.study.entity.MemberToken;
import com.superbox.study.repository.MemberRepository;
import com.superbox.study.repository.MemberTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    @Value("${superbox.app.jwtExpirationSeconds}")
    private int expirationSeconds;
    private final MemberRepository memberRepository;
    private final MemberTokenRepository memberTokenRepository;

    @Transactional
    public Optional<MemberToken> findByToken(String refreshToken) {
        return memberTokenRepository.findByToken(refreshToken);
    }

    public MemberToken verifyExpiration(MemberToken token) {
        // TODO 중복 허용을 할 것 인지에 따라 1개의 데이터만 관리
        // 만료시 삭제
        if (token.getExpireAt().compareTo(LocalDateTime.now()) < 0) {
            memberTokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Failed for [%s]: Refresh token was expired. Please make a new signin request", token.getToken()));
        }
        return token;
    }

    @Transactional
    public MemberToken createRefreshToken(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "에러: 회원 정보가 없습니다."));

        MemberToken memberToken = MemberToken.builder()
                .member(member)
                .token(UUID.randomUUID().toString())
                .expireAt(LocalDateTime.now().plusSeconds(expirationSeconds))
                .build();

        return memberTokenRepository.save(memberToken);
    }

    @Transactional
    public MemberToken postponeExpiryAt(MemberToken memberToken) {
        memberToken.setExpireAt(LocalDateTime.now().plusSeconds(expirationSeconds));
        return memberToken;
    }
}
