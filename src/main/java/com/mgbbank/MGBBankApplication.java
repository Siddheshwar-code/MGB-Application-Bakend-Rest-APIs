package com.mgbbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
		
		info = @Info(
				title = "Maharashtra Gramin Bank",
				
				description = "Backend Rest APIs for MGB Bank",
				
				version = "v1.0",
				
				contact = @Contact(
						
						name = "Siddheshwar Salunke",
						
						email = "siddheshwarsalunke2001@gmail.com"
						),
				license = @License(
						name = " Maharashtra Gramin Bank"
						)
				),
		externalDocs = @ExternalDocumentation(
				description = "Maharashtra Gramin Bank App Documentation"
				)
		)
public class MGBBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(MGBBankApplication.class, args);
	}

}
