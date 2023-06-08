package jass.security;

import ClickSend.ApiClient;
import jass.security.controller.AuthenticationController;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SecurityApplication {
    //Clicksend
    @Value("${clicksendUsername}")
    private String clickSendUsername;
    @Value("${clicksendApiKey}")
    private String clickSendApiKey;
    private static final Logger logger = LoggerFactory.getLogger(SecurityApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
        logger.warn("Empty search history tells more then full one ~ Confucius");
        logger.info("ZNATNO BOLJE NEGO MIRKOSERVISI I GOLANG I DOCKER");
        logger.error("ERROR ERROR ERROR ERROR ERROR ERROR ERROR ");
        System.out.println("JSSA");
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ApiClient clickSendConfig(){
        ApiClient clickSendApiClient = new ApiClient();
        clickSendApiClient.setUsername(clickSendUsername);
        clickSendApiClient.setPassword(clickSendApiKey);
        return clickSendApiClient;
    }
}
