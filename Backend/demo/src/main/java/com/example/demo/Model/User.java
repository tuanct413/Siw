package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.Email;

@Entity
@Table(name = "Users") // tên bảng trong MySQL
@Data  // tự động sinh getter, setter, toString, equals, hashCode
@NoArgsConstructor // tự động sinh constructor rỗng
@AllArgsConstructor // tự động sinh constructor với tất cả field
public class User {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 150)
    @Email(message = "Email Không đúng định dạng")
    @NotBlank(message = "Email Không được để trống")
    private String email;

    @NotBlank(message = "Password Không được để trống")
    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    @JsonIgnore
    private String password;

    @JsonIgnore
    private boolean verified; // trạng thái xác thực

    @JsonIgnore
    private String verificationCode; // mã OTP

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        USER,
        ADMIN
    }

}
