package com.bigleague.ttp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bigleague.resource"})
public class TtpApplication {

	public static void main(String[] args) {
		SpringApplication.run(TtpApplication.class, args);
	}

}
