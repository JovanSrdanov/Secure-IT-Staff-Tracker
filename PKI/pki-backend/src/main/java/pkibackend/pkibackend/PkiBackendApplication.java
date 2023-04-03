package pkibackend.pkibackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
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
@OpenAPIDefinition(info = @Info(title = "PKI", version = "0.1", description = "PKI FOR BUSEP 22/23"))

public class PkiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PkiBackendApplication.class, args);
	}
	@Bean
	public Validator validator() {
		ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure().buildValidatorFactory();
		return validatorFactory.getValidator();
	}


}
