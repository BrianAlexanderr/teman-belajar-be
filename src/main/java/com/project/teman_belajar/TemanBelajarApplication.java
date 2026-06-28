package com.project.teman_belajar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TemanBelajarApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemanBelajarApplication.class, args);
	}

}
