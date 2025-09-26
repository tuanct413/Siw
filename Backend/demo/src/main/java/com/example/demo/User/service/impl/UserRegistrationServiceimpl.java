package com.example.demo.User.service.impl;

import com.example.demo.User.model.User;
import com.example.demo.User.repository.UserRepository;
import com.example.demo.User.service.Interface.UserRegistrationService;
import com.example.demo.common.mail.MailService;
import com.example.demo.common.security.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Service
public class UserRegistrationServiceimpl implements UserRegistrationService {
    private final UserRepository userRepository;

    private final JwtService jwtService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public UserRegistrationServiceimpl(UserRepository userRepository , JwtService jwtService,MailService mailService) {
        this.userRepository = userRepository;
        this.jwtService=jwtService;
        this.mailService = mailService;
    }
    @Override
    public ResponseEntity<Map<String, Object>> createUser(User user) throws MessagingException {
        Map<String,Object> response = new HashMap<>();

        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            if (!existingUser.isVerified()) {
                // User chưa verify → gửi lại OTP
                String code = mailService.sendRandomCodeEmail(existingUser.getEmail(),"Xác thực Email");
                existingUser.setVerificationCode(code);
                userRepository.save(existingUser);

                response.put("message", "User chưa xác thực. OTP đã được gửi lại.");
                response.put("data", Map.of(
                        "email", existingUser.getEmail(),
                        "name", existingUser.getName()


                ));
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                // User đã verify → báo lỗi
                response.put("message", "Email đã tồn tại");
                response.put("data", "error");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Nếu user chưa tồn tại → tạo mới
        String code = mailService.sendRandomCodeEmail(user.getEmail(),"Xác thực Email");
        user.setVerificationCode(code);
        user.setVerified(false); // chưa xác thực
        user.setRole(User.Role.USER);

        userRepository.save(user); // lưu user tạm vào DB

        response.put("message", "User tạo thành công tạm thời. Vui lòng xác thực OTP.");
        response.put("data", Map.of("email", user.getEmail(), "name", user.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Override
    public ResponseEntity<Map<String, Object>> verifyUser(String email, String code) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            response.put("message", "User không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if(user.isVerified()){
            response.put("message", "User Đã Verified");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (code.equals(user.getVerificationCode())) {
            user.setVerified(true);
            userRepository.save(user); // lưu user vào DB sau khi OTP đúng
            response.put("message", "Xác thực thành công. User đã được lưu.");
            response.put("data", Map.of("email", user.getEmail(), "name", user.getName() ,"verify", user.isVerified() ));
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Mã OTP không đúng");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    @Override
    public ResponseEntity<Map<String,Object>> getverfify(String email) throws MessagingException {
        Map<String, Object> response = new HashMap<>();

        // Tìm user ignore case, trim email
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email.trim());

        if(userOpt.isEmpty()){
            response.put("message", "Không tồn tại email này");
            System.out.println(email);
            response.put("data", "error");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        User user = userOpt.get(); // dùng user đã kiểm tra tồn tại
        String code;

        if(!user.isVerified()){
            // Chưa xác thực → gửi OTP xác thực email
            code = mailService.sendRandomCodeEmail(email, "Xác thực Email");
            response.put("message", "User chưa xác thực. OTP đã được gửi lại để xác thực email.");
        } else {
            // Đã xác thực → gửi OTP để đổi mật khẩu
            code = mailService.sendRandomCodeEmail(email, "Đổi mật khẩu");
            response.put("message", "OTP đã được gửi để đổi mật khẩu.");
        }

        user.setVerificationCode(code);
        userRepository.save(user);

        response.put("data", Map.of(
                "email", user.getEmail(),
                "name", user.getName()
        ));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
