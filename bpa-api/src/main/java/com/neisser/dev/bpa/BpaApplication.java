package com.neisser.dev.bpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BpaApplication.class, args);
	}

}
