package jass.security;

import ClickSend.ApiClient;
import org.modelmapper.ModelMapper;
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

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
        System.Logger logger = System.getLogger(SecurityApplication.class.getName());
        logger.log(System.Logger.Level.INFO, "Empty search history tells more then full one ~ Confucius");
        System.out.println("ZNATNO BOLJE NEGO MIRKOSERVISI I GOLANG I DOCKER");
        System.out.println("Weakness disgusts me");
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
