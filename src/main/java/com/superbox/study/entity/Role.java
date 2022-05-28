package com.superbox.study.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 권한 코드 정보
 */
@Getter
@Setter
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    private ERole name;

    public enum ERole {
        ROLE_MEMBER,
        ROLE_ADMIN,
        ROLE_AGENCY
    }
}
