package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors()  // Enable CORS, dùng WebConfig
            .and()
            .csrf().disable()  // Tạm disable CSRF cho test
            .authorizeHttpRequests()
            .requestMatchers("/users/**").permitAll()  // Cho phép frontend gọi test API
            .anyRequest().authenticated();  // Các endpoint khác vẫn cần auth

        return http.build();
    }
}
