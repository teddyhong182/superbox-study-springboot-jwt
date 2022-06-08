package com.superbox.study.repository;

import com.superbox.study.entity.MemberToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberTokenRepository extends JpaRepository<MemberToken, Long> {
    Optional<MemberToken> findByToken(String token);
}