package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.dto.CreateCertificateInfo;
import pkibackend.pkibackend.model.Certificate;

import java.text.ParseException;
import java.util.Date;

public interface ICertificateService extends ICrudService<Certificate>{
    Certificate generateCertificate(CreateCertificateInfo info) throws RuntimeException;
}
