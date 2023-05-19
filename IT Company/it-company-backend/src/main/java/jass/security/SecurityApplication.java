package jass.security;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
        System.Logger logger = System.getLogger(SecurityApplication.class.getName());
        logger.log(System.Logger.Level.INFO, "Empty search history tells more then full one ~ Confucius");
        System.out.println("ZNATNO BOLJE NEGO MIRKOSERVISI I GOLANG I DOCKER");
        System.out.println("Weakness disgusts me");
    }
}
