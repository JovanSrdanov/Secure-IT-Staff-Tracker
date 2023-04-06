package pkibackend.pkibackend.service.interfaces;

import org.springframework.http.ResponseEntity;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.model.Certificate;

import java.util.Date;

public interface ICertificateService extends ICrudService<Certificate>{
    void generateCertificate(Certificate certificate);
}
