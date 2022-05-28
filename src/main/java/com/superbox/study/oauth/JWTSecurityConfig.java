package com.superbox.study.oauth;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configurable
public class JWTSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(authz -> authz
                        .antMatchers(HttpMethod.GET, "/foos/**").hasAuthority("SCOPE_read")
                        .antMatchers(HttpMethod.POST, "/foos").hasAuthority("SCOPE_write")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        // 읽기범위가 있는 액세스 토큰을 가진 사람은 GET /foos 가능
        // 쓰기범위가 있는 액세스 토근을 가진 사람은 POST /foos 가능
        // oauth2ResourceServer 는 jwt 호출을 필요.

    }
}
