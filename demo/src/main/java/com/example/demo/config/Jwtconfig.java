package com.example.demo.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Configurable
@Component
public class Jwtconfig {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expirationTime;
    public String getSecretKey() {
        return secretKey;
    }

    public long getexpirationTime() {
        return expirationTime;
    }




}
