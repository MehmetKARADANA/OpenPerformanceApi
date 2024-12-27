package com.mehmetkaradana.springboot_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@EnableAsync
public class SpringbootApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApiApplication.class, args);
	}
}
