package com.example.demo.controller;

import com.example.demo.DTO.LoginRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.Implementation.UserServiceinterface;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JwtService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserServiceinterface userService;
    private final JwtService jwtService;

    public UserController(UserServiceinterface userService,JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;

    }

    @GetMapping("/find")
    public Map<String, Object> findUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    // API tạo user mới
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createUser(
            @Valid @RequestBody User user,
            BindingResult result) throws MessagingException {

        Map<String, Object> response = new HashMap<>();

        // Kiểm tra lỗi validate
        if (result.hasErrors()) {
            response.put("message", result.getAllErrors().get(0).getDefaultMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Gọi service tạo user tạm và gửi OTP
        // userService.createUser() chỉ gửi mail và trả về thông tin tạm, chưa lưu DB
        ResponseEntity<Map<String, Object>> serviceResponse = userService.createUser(user);

        return serviceResponse;
    }
    // API xác thực OTP
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyUser(
            @RequestParam String email,
            @RequestParam String code) {

        // Gọi service để xác thực OTP
        ResponseEntity<Map<String, Object>> response = userService.verifyUser(email, code);

        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDTO request, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", result.getAllErrors().get(0).getDefaultMessage());
            response.put("data", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return userService.login(request.getEmail(), request.getPassword());
    }


}
