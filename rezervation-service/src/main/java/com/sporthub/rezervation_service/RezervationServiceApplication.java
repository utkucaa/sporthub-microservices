package com.sporthub.rezervation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.sporthub.rezervation_service.client"})
@EnableScheduling
public class RezervationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RezervationServiceApplication.class, args);
	}

}
