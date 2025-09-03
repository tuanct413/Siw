package com.example.demo.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "locations") // nên dùng số nhiều, convention
@Data  // tự động sinh getter, setter, toString, equals, hashCode
@NoArgsConstructor // tự động sinh constructor rỗng
@AllArgsConstructor // tự động sinh constructor với tất cả field
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên city_name Không được để trống")
    @Column(nullable = false)
    private String cityName;


    @NotBlank(message = "Tên country Không được để trống")
    @Column(nullable = false)
    private String country;

    // Mỗi location sẽ thuộc về 1 user



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // FK user_id
    @JsonIgnore
    private User user;


    private LocalDate created_at;



}
