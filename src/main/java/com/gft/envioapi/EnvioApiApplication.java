package com.gft.envioapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class EnvioApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnvioApiApplication.class, args);
	}

}
