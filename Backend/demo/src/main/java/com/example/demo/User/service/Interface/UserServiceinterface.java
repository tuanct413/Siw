package com.example.demo.User.service.Interface;

import com.example.demo.User.DTO.ForgotPasswordDTO;
import com.example.demo.User.DTO.UpdateProfileDTO;
import com.example.demo.User.model.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserServiceinterface {

    List<User> getAllUsers();
    Map<String, Object> findByEmail(String email);
    ResponseEntity<Map<String, Object>> login(String email, String password, HttpServletResponse response);






    ResponseEntity<Map<String, Object>> forgotPassword(ForgotPasswordDTO forgotPasswordDTO);


    ResponseEntity<Map<String,Object>> getProfile(Long token);


    ResponseEntity<Map<String, Object>> updateUser(UpdateProfileDTO updateProfileDTO , Long userId) throws MessagingException;
}
