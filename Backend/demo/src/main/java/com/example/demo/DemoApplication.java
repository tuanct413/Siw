package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("Application started successfully!");
	}
	@Bean
	public CommandLineRunner checkDataSource(DataSource dataSource) {
		return args -> {
			System.out.println("🔎 Database đang kết nối: " + dataSource.getConnection().getMetaData().getURL());
		};
	}


}
