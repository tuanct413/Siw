package com.example.demo.service;


import com.example.demo.config.Jwtconfig;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import io.jsonwebtoken.security.Keys;
import org.springframework.web.server.ResponseStatusException;


@Service
public class JwtService {

    private final Jwtconfig jwtconfig;
    private final Key key;

    public JwtService(Jwtconfig jwtconfig)
    {
        this.jwtconfig = jwtconfig;
        // Plain secret, không decode Base64
        this.key = Keys.hmacShaKeyFor(jwtconfig.getSecretKey().getBytes());
    }

    //tạo chuỗi Jwt
    public String generateToken(long userid , String email){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtconfig.getexpirationTime());

        return Jwts.builder()
                .setSubject(String.valueOf(userid))
                .claim("email",email)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    // Cách 1: Sử dụng method isTokenExpired(String token) có sẵn
    public boolean verifyToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            parseToken(token); // Chỉ cần parse để kiểm tra token hợp lệ
            return !isTokenExpired(token); // Dùng method có sẵn nhận String
        } catch (Exception e) {
            return false;
        }
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    // Method isTokenExpired - nhận String parameter
    private boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true; // Nếu có lỗi thì coi như expired
        }
    }
    // Lấy userId từ token
    public Long extractUserId(String token) {
        Claims claims = parseToken(token);
        String subject = claims.getSubject(); // vì khi generateToken bạn setSubject(userid)
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new RuntimeException("UserId trong token không hợp lệ", e);
        }
    }


}


