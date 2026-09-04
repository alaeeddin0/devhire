package com.example.devhire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DevhireApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevhireApplication.class, args);
	}

}
