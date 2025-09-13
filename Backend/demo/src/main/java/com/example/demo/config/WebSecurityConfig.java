package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;


    public WebSecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;

    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public GET request
                        .requestMatchers(HttpMethod.GET, "/weather/**").permitAll()

                        // POST/PUT/DELETE cần token
                        .requestMatchers(HttpMethod.POST, "/weather/**").authenticated()

                        // Public users API
                        .requestMatchers("/users/create").permitAll()
                        .requestMatchers("/users/verify").permitAll()
                        .requestMatchers("/users/login").permitAll()
                        .requestMatchers("/users/forgot-password").permitAll()
                        .requestMatchers("/users/v1/verify").permitAll()

                        // Reports API: cần token
                        .requestMatchers("/api/reports/**").authenticated()

                        // Các request còn lại cũng cần token
                        .anyRequest().authenticated()
                )

                // Thêm JWT Filter trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
