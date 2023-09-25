package com.procurement.system.construction.industry;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Tag(name = "Test Controller")
public class ConstructionIndustryApplication {

	@Value("${server.port}")
	private int serverPort;

	public static void main(String[] args) {
		SpringApplication.run(ConstructionIndustryApplication.class, args);
	}

	@GetMapping("/")
	public String testMessage(){
		return "Application Running Well on Port " + serverPort;
	}
}
