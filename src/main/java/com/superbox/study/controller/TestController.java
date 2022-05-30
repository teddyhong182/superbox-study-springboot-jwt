package com.superbox.study.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Slf4j
@RestController
@RequestMapping(value = "/api/test")
public class TestController {

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content";
    }

    @GetMapping("/member")
    @PreAuthorize("hasRole('MEMBER') or hasRole('AGENCY') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content";
    }

    @GetMapping("/agency")
    @PreAuthorize("hasRole('AGENCY')")
    public String agencyAccess() {
        return "Agency Board";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board";
    }
}
