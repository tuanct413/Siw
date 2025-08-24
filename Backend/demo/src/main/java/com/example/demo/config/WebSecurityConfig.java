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
                .cors()
                .and()
                .csrf().disable()
                .authorizeHttpRequests()
                //Public API không cần token
                .requestMatchers("/users/**").permitAll()     // Cho phép users API
                .requestMatchers("/weather/**").permitAll()   //  Cho phép weather API

                //   //  API yêu cầu token (JWT/session)
                //            .requestMatchers("/users/**").authenticated()
                //            .requestMatchers("/profile/**").authenticated()
                //
                //            //  API chỉ cho ADMIN
                //            .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();                // Các API khác yêu cầu auth

        return http.build();
    }
}
