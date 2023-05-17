package jass.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
		System.Logger logger = System.getLogger(SecurityApplication.class.getName());
		logger.log(System.Logger.Level.INFO, "Empty search history tells more then full one ~ Confucius");
	}

}
