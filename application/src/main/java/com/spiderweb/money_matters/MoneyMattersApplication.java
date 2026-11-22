package com.spiderweb.money_matters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.spiderweb")
@EnableScheduling
public class MoneyMattersApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyMattersApplication.class, args);
	}

}
