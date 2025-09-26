package com.example.demo.User.controller;

import com.example.demo.User.model.User;
import com.example.demo.User.service.Interface.UserRegistrationService;
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
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }
    @GetMapping("/v1/verify")
    public ResponseEntity<Map<String, Object>> getVerify(@RequestParam String email) throws MessagingException {
        return userRegistrationService.getverfify(email);
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
        ResponseEntity<Map<String, Object>> serviceResponse = userRegistrationService.createUser(user);

        return serviceResponse;
    }
    // API xác thực OTP
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyUser(
            @RequestParam String email,
            @RequestParam String code) {

        // Gọi service để xác thực OTP
        ResponseEntity<Map<String, Object>> response = userRegistrationService.verifyUser(email, code);

        return response;
    }

}
