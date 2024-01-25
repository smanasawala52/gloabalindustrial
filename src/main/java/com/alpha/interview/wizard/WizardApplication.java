package com.alpha.interview.wizard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@ComponentScan(basePackages = "com.alpha.interview")  // Adjust the package name accordingly
@EnableWebSocketMessageBroker
public class WizardApplication {

	public static void main(String[] args) {
		SpringApplication.run(WizardApplication.class, args);
	}

}
