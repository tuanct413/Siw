package com.example.demo.repository;



import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // có thể thêm query custom nếu cần
    User findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByVerificationCode(String verificationCode);
}
