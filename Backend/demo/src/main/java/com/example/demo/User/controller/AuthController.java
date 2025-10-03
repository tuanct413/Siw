package com.example.demo.User.controller;

import com.example.demo.User.DTO.ForgotPasswordDTO;
import com.example.demo.User.DTO.LoginRequestDTO;
import com.example.demo.User.service.Interface.UserServiceinterface;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserServiceinterface userService;

    public AuthController(UserServiceinterface userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequestDTO request,
            BindingResult result,
            HttpServletResponse response) {

        if (result.hasErrors()) {
            Map<String, Object> res = new HashMap<>();
            res.put("message", result.getAllErrors().get(0).getDefaultMessage());
            res.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        return userService.login(request.getEmail(), request.getPassword(), response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody ForgotPasswordDTO dto) {
        return userService.forgotPassword(dto);
    }
}
