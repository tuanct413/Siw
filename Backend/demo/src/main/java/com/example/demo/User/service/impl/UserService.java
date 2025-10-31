package com.example.demo.User.service.impl;

import com.example.demo.User.DTO.ForgotPasswordDTO;
import com.example.demo.User.DTO.UpdateProfileDTO;
import com.example.demo.User.model.User;
import com.example.demo.User.repository.UserRepository;
import com.example.demo.common.security.JwtService;
import com.example.demo.common.mail.MailService;
import com.example.demo.User.service.Interface.UserServiceinterface;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class UserService implements UserServiceinterface {

    private final UserRepository userRepository;

    private final JwtService jwtService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public UserService(UserRepository userRepository , JwtService jwtService,MailService mailService) {
        this.userRepository = userRepository;
        this.jwtService=jwtService;
        this.mailService = mailService;
    }

    // Xác thực OTP

    @Override
    public ResponseEntity<Map<String, Object>> updateUser(UpdateProfileDTO updateProfileDTO, Long userId) throws MessagingException {
        Map<String, Object> response = new HashMap<>();
        Optional<User> existingUserOpt = userRepository.findById(userId);

        if (existingUserOpt.isPresent()) {
            User exitingUser = existingUserOpt.get();
            if (!exitingUser.getId().equals(userId)) {
                response.put("message", "Bạn không có quyền cập nhật user này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            exitingUser.setName(updateProfileDTO.getName());
            if(!exitingUser.getEmail().equals(updateProfileDTO.getEmail())){
                exitingUser.setEmail(updateProfileDTO.getEmail());
                exitingUser.setVerified(false);
                String code = mailService.sendRandomCodeEmail(updateProfileDTO.getEmail(),"Xác thực Email");
                exitingUser.setVerificationCode(code);
                userRepository.save(exitingUser);
            }
            response.put("message", "Cập nhật thành công");

            return ResponseEntity.ok(response);
        }
        else {
            response.put("message", "Không tìm thấy user với ID: " + userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

   @Override
    public ResponseEntity<Map<String, Object>> login(String email, String password, HttpServletResponse response) {
        Map<String, Object> res = new HashMap<>();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            res.put("message", "Email hoặc mật khẩu không chính xác");
            res.put("data", "error");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean match = encoder.matches(password, user.getPassword());
        if (!match) {
            res.put("message", "Email hoặc mật khẩu không chính xác");
            res.put("data", "error");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        if (!user.isVerified()) {
            res.put("message", "Chưa xác thực Email");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        // 🔹 Login thành công
        String token = jwtService.generateToken(user.getId(), user.getEmail());

        // Tạo cookie HttpOnly
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)   // để dev = false, deploy HTTPS thì true
                .path("/")
                .maxAge(24 * 60 * 60) // 1 ngày
                  .sameSite("Lax") 
                .build();

        // Gắn cookie vào response
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Trả dữ liệu user (không trả token trong body nữa)
        Map<String, Object> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("email", user.getEmail());

        

        res.put("message", "Đăng nhập thành công");
        res.put("token",token);
        res.put("data", data);
        return ResponseEntity.ok(res);
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Map<String, Object> findByEmail(String email){
        Map<String, Object>  response = new HashMap<>();
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByEmail(email));
        if(userOpt.isPresent()){
            User user = userOpt.get();
            response.put("email",user.getEmail());
            response.put("password",user.getPassword());
            response.put("name",user.getName());
            response.put("id",user.getId());
        }
        else {
            response.put("error", "User not found with email: " + email);
        }
        return response;
    }

    public ResponseEntity<Map<String, Object>> forgotPassword(ForgotPasswordDTO forgotPasswordDTO){
        Map<String, Object> response = new HashMap<>();

        String email = forgotPasswordDTO.getEmail().trim();
        String verify = forgotPasswordDTO.getVerificationCode();

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            response.put("message", "Không tồn tại email này");
            response.put("data", "error");
            return ResponseEntity.ok(response);
        }
        Optional<User> userOptByCode = userRepository.findByVerificationCode(verify);
        if (userOptByCode.isEmpty()) {
            response.put("message", "OTP không đúng");
            response.put("data", "error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        User user = userOptByCode.get();
        user.setPassword(passwordEncoder.encode(forgotPasswordDTO.getPassword()));
        userRepository.save(user);

        response.put("message", "Xác thực thành công. đổi mật khẩu thành công.");
        response.put("data", Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "verify", user.isVerified()
        ));
        return ResponseEntity.ok(response);
    }
    public ResponseEntity<Map<String,Object>> getProfile(Long userId) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            response.put("message", "Không tìm thấy user với ID: " + userId);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User user = userOpt.get();
        response.put("message", "Thành công");
        response.put("data", user); // hoặc map sang DTO nếu muốn giấu bớt thông tin nhạy cảm

        return ResponseEntity.ok(response);
    }
}
