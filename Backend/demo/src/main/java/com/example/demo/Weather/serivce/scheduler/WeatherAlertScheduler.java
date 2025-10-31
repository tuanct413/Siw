package com.example.demo.Weather.serivce.scheduler;

import com.example.demo.Weather.Repository.WeatherAlertRepository;
import com.example.demo.Weather.model.WeatherAlert;
import com.example.demo.Weather.serivce.impl.WeatherNotificationService;
import com.example.demo.Weather.serivce.impl.WeatherService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class WeatherAlertScheduler {
    private final  WeatherService weatherService;
    private final WeatherAlertRepository weatherAlertRepository;
    public WeatherAlertScheduler (WeatherService weatherService,WeatherAlertRepository weatherAlertRepository){
        this.weatherService = weatherService;
        this.weatherAlertRepository = weatherAlertRepository;
    }
    // Chạy mỗi 3 tiếng 1 lần
    @Scheduled(cron = "0 0 */3 * * *") //3 hours 1 lần
    public void runWeatherJob() {
        System.out.println("⏰ Tự động gọi job gửi cảnh báo...");
        List<WeatherAlert> activeAlerts = weatherAlertRepository.findByActiveTrue();
        for(WeatherAlert alert : activeAlerts){
            long userID =  alert.getUser().getId();
            String city = alert.getCity();
            String condition = alert.getCondition();
            System.out.println("🚀 Bắt đầu setWeatherAlert cho userId=" + userID + ", city=" + city + ", condition=" + condition);;
            // Kiểm tra thời gian gửi gần nhất (chỉ gửi nếu đã qua 3 giờ)
            if(alert.getLastSent() == null || alert.getLastSent().isBefore(LocalDateTime.now().minusHours(3))){
               weatherService.setWeatherAlert(userID,city,condition);
               alert.setLastSent(LocalDateTime.now());


                weatherAlertRepository.save(alert);
            }

        }
        System.out.println("✅ Hoàn tất job tự động gửi cảnh báo!");
    }
}
