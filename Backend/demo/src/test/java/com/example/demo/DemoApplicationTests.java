package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // load application-test.properties
class DemoApplicationTests {

	@Test
	void contextLoads() {
		// Chỉ test context load, không cần DB thật
	}
}
