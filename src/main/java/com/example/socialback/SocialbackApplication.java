package com.example.socialback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration; // Add this import



@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})


public class SocialbackApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialbackApplication.class, args);
	}

}
