package com.example.demo.Implementation;

import com.example.demo.entity.User;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserServiceinterface {
    ResponseEntity<Map<String, Object>> createUser(User user) throws MessagingException;
    List<User> getAllUsers();
    Map<String, Object> findByEmail(String email);
    ResponseEntity<Map<String, Object>> login(String email, String password);

    ResponseEntity<Map<String, Object>> verifyUser(String email, String code);
}
