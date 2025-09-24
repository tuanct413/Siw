package com.example.demo.service.User;

import com.example.demo.DTO.ForgotPasswordDTO;
import com.example.demo.DTO.UpdateProfileDTO;
import com.example.demo.Model.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserServiceinterface {
    ResponseEntity<Map<String, Object>> createUser(User user) throws MessagingException;
    List<User> getAllUsers();
    Map<String, Object> findByEmail(String email);
 ResponseEntity<Map<String, Object>> login(String email, String password, HttpServletResponse response);


    ResponseEntity<Map<String, Object>> verifyUser(String email, String code);

    ResponseEntity<Map<String,Object>> getverfify(String email) throws MessagingException;

    ResponseEntity<Map<String, Object>> forgotPassword(ForgotPasswordDTO forgotPasswordDTO);


    ResponseEntity<Map<String,Object>> getProfile(Long token);


    ResponseEntity<Map<String, Object>> updateUser(UpdateProfileDTO updateProfileDTO , Long userId) throws MessagingException;
}
