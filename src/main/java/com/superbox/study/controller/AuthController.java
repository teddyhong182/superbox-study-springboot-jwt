package com.superbox.study.controller;

import com.superbox.study.config.security.JwtUtils;
import com.superbox.study.config.security.UserDetailsImpl;
import com.superbox.study.entity.Member;
import com.superbox.study.entity.MemberToken;
import com.superbox.study.entity.Role;
import com.superbox.study.payload.*;
import com.superbox.study.repository.MemberRepository;
import com.superbox.study.repository.RoleRepository;
import com.superbox.study.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateMember(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        MemberToken memberToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(JwtResponse.builder()
                .token(jwt).id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .refreshToken(memberToken.getToken())
                .build());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerMember(@RequestBody SignupRequest request) {
        if (memberRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("?????? : ???????????? ?????? ???????????????."));
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("?????? : ???????????? ?????? ???????????????."));
        }
        // create new member's account
        Member member = Member.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        Role role = roleRepository.findByName(Role.ERole.ROLE_MEMBER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "??????: ???????????? ????????? ????????????."));
        // ??????
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        member.setRoles(roles);
        memberRepository.save(member);
        return ResponseEntity.ok(new MessageResponse("?????????????????????."));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshTokenService::postponeExpiryAt)
                .map(MemberToken::getMember)
                .map(member -> {
                    String token = jwtUtils.generateTokenFromUsername(member.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Failed for [%s]: Refresh token is not in database!", requestRefreshToken)));
    }
}
