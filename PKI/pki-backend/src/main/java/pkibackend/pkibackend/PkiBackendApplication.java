package pkibackend.pkibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
public class PkiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PkiBackendApplication.class, args);
        System.out.println("THREAT MODEL FORMED.");
        System.out.println("INITIATE SAFETY MESURES.");
    }

    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure().buildValidatorFactory();
        return validatorFactory.getValidator();
    }


}
