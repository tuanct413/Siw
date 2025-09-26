package com.example.demo.User.service.Interface;

import com.example.demo.User.model.User;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserRegistrationService {
    ResponseEntity<Map<String, Object>> createUser(User user) throws MessagingException;
    ResponseEntity<Map<String, Object>> verifyUser(String email, String code);
    ResponseEntity<Map<String,Object>> getverfify(String email) throws MessagingException;
}
