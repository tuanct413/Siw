package com.example.demo.User.controller;

import com.example.demo.User.DTO.CustomUserDetails;
import com.example.demo.User.DTO.ForgotPasswordDTO;
import com.example.demo.User.DTO.LoginRequestDTO;
import com.example.demo.User.DTO.UpdateProfileDTO;
import com.example.demo.Weather.serivce.Interface.WeatherServiceInterface;
import com.example.demo.User.model.User;
import com.example.demo.User.service.Interface.UserServiceinterface;
import com.example.demo.common.security.JwtService;
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
    private final WeatherServiceInterface weatherServiceImp;

    public UserController(UserServiceinterface userService, WeatherServiceInterface weatherServiceImp) {
        this.userService = userService;
        this.weatherServiceImp = weatherServiceImp;
    }

    @GetMapping("/find")
    public Map<String, Object> findUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
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

    @GetMapping("/profile")
    public ResponseEntity<Map<String,Object>> getProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId(); // lấy id từ custom details
        return userService.getProfile(userId);
    }

}
