package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.dto.CreateCertificateInfo;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.exceptions.InternalServerErrorException;
import pkibackend.pkibackend.model.Certificate;

import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Date;

public interface ICertificateService extends ICrudService<Certificate>{
    Certificate generateCertificate(CreateCertificateInfo info) throws RuntimeException, BadRequestException, CertificateEncodingException, InternalServerErrorException;
    public void revoke(BigInteger certSerialNum);
    public boolean isRevoked(BigInteger certSerialNum);
    X509Certificate GetCertificateBySerialNumber(BigInteger serialNumber);
    boolean isChainValid(BigInteger certSerialNum);
}
