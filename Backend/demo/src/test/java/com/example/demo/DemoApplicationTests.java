package com.example.demo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Skip context load test")
class DemoApplicationTests {

    @Test
    void contextLoads() {
        // Chỉ load ApplicationContext, không cần database
    }
}
