package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.dto.CertificateInfoDto;
import pkibackend.pkibackend.dto.CreateCertificateInfo;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.exceptions.InternalServerErrorException;
import pkibackend.pkibackend.model.Certificate;

import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

public interface ICertificateService extends ICrudService<Certificate>{
    Certificate generateCertificate(CreateCertificateInfo info) throws RuntimeException, BadRequestException, CertificateEncodingException, InternalServerErrorException;
    Iterable<CertificateInfoDto> findAllAdmin();
    Iterable<CertificateInfoDto> findAllCaAdmin();
    Iterable<CertificateInfoDto> findAllForLoggedIn(UUID accountId);
    Iterable<CertificateInfoDto> findAllValidCaForLoggedIn(UUID accountId);
    public void revoke(BigInteger certSerialNum) throws BadRequestException;
    public boolean isRevoked(BigInteger certSerialNum) throws BadRequestException;
    X509Certificate GetCertificateBySerialNumber(BigInteger serialNumber) throws BadRequestException;
    boolean isChainValid(BigInteger certSerialNum) throws BadRequestException;
}
