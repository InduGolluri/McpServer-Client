package com.example.demoServer;

import java.util.List;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demoServer.tools.EmailToolsService;

//Marks this as the main Spring Boot application
@SpringBootApplication 
public class DemoServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoServerApplication.class, args); // Starts the Spring Boot app
	}

	
	// Registers tool callbacks so the AI model can call EmailToolsService methods
	@Bean
	public List<ToolCallback> toolCallbacks(EmailToolsService emailToolsService) {
		return List.of(ToolCallbacks.from(emailToolsService)); // Converts EmailToolsService into a tool callback
	}
}
