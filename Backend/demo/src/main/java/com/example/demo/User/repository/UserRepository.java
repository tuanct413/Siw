package com.example.demo.User.repository;



import com.example.demo.User.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // có thể thêm query custom nếu cần
    User findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByVerificationCode(String verificationCode);

    @Query("SELECT u.email FROM User u WHERE u.id = :id")
    String findEmailById(@Param("id") Long id);

}
