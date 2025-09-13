package com.example.demo.controller;

import com.example.demo.DTO.CustomUserDetails;
import com.example.demo.DTO.ForgotPasswordDTO;
import com.example.demo.DTO.LoginRequestDTO;
import com.example.demo.DTO.UpdateProfileDTO;
import com.example.demo.service.Implementation.WeatherServiceImp;
import com.example.demo.Model.User;
import com.example.demo.service.Implementation.UserServiceinterface;
import com.example.demo.service.JwtService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserServiceinterface userService;
    private final JwtService jwtService;
    private final WeatherServiceImp weatherServiceImp;

    public UserController(UserServiceinterface userService,JwtService jwtService,WeatherServiceImp weatherServiceImp) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.weatherServiceImp = weatherServiceImp;

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

    @GetMapping("/getall")
    public List<User> getAllUsers(){

        return userService.getAllUsers();
    }
    @GetMapping("/getlocation")
    public ResponseEntity<Map<String, Object>> getAllHistory(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId(); // Lấy userId dễ dàng
        return weatherServiceImp.getallhistory(userId);
    }
    @PatchMapping("/update")
    public ResponseEntity<Map<String,Object>> updateUser(@Valid @RequestBody UpdateProfileDTO dto,
                                                         Authentication authentication) throws MessagingException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        return userService.updateUser(dto, userId);
    }



    @GetMapping("/v1/verify")
    public ResponseEntity<Map<String, Object>> getVerify(@RequestParam String email) throws MessagingException {
        return userService.getverfify(email);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        // Gọi thẳng service
        return userService.forgotPassword(forgotPasswordDTO);
    }
    @GetMapping("/profile")
    public ResponseEntity<Map<String,Object>> getProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId(); // lấy id từ custom details
        return userService.getProfile(userId);
    }









}
