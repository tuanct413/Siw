package com.example.demo.Weather.model;

import com.example.demo.User.model.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "weather_alert")
@Getter
@Setter
public class WeatherAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người dùng liên kết
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Thành phố cần cảnh báo
    @Column(nullable = false)
    private String city;

    // Điều kiện cảnh báo (VD: Rain, Snow...)
    @Column(name = "weather_condition", nullable = false)
    private String condition;

    // Trạng thái có kích hoạt cảnh báo hay không
    private boolean active = true;

    // Lưu thời điểm gửi gần nhất (để tránh gửi trùng)
    private java.time.LocalDateTime lastSent;

    public WeatherAlert(User user, String city, String condition) {
        this.user = user;
        this.city = city;
        this.condition = condition;
    }

    // Getter/setter...
    // (tạo bằng Lombok nếu muốn)
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public java.time.LocalDateTime getLastSent() { return lastSent; }
    public void setLastSent(java.time.LocalDateTime lastSent) { this.lastSent = lastSent; }
}
