package pkibackend.pkibackend.service.implementations;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pkibackend.pkibackend.Utilities.Keys;
import pkibackend.pkibackend.certificates.CertificateGenerator;
import pkibackend.pkibackend.dto.CreateCertificateInfo;
import pkibackend.pkibackend.dto.EntityInfo;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.repository.AccountRepository;
import pkibackend.pkibackend.repository.CertificateRepository;
import pkibackend.pkibackend.service.interfaces.ICertificateService;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.UUID;

@Service
@Primary
public class CertificateService implements ICertificateService {
    private final CertificateRepository _certificateRepository;
    private final AccountRepository _accountRepository;

    @Value("${keystorePassword}")
    private String keyStorePassword;

    @Autowired
    public CertificateService(CertificateRepository certificateRepository, AccountRepository accountRepository) {
        _certificateRepository = certificateRepository;
        _accountRepository = accountRepository;
    }

    @Override
    public Iterable<Certificate> findAll() {
        return null;
    }

    @Override
    public Certificate findById(UUID id) {
        return null;
    }

    @Override
    public Certificate save(Certificate entity) throws BadRequestException {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    // TODO Stefan: treba namestiti transakciju ovde
    @Override
    public Certificate generateCertificate(CreateCertificateInfo info) throws RuntimeException {
        Account subject = buildEntity(info.getSubjectInfo());
        Account issuer = buildEntity(info.getIssuerInfo());

        UUID serialNumber = UUID.randomUUID();

        X509Certificate certificate = CertificateGenerator.generateCertificate(subject,
                issuer, info.getStartDate(), info.getEndDate(), serialNumber);

        Certificate newCertificate = new Certificate(issuer, serialNumber,
                info.getStartDate(), info.getEndDate(), certificate);

        _certificateRepository.SaveCertificate(
                newCertificate,
                keyStorePassword, info.getAlias(),
                "keyPassword");

        return newCertificate;
    }

    private Account buildEntity(EntityInfo info) {
        UUID accountId = UUID.randomUUID();

        KeyPair kp = Keys.generateKeyPair();
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, info.getCommonName());
        builder.addRDN(BCStyle.SURNAME, info.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, info.getGivenName());
        builder.addRDN(BCStyle.O, info.getOrganization());
        builder.addRDN(BCStyle.OU, info.getOrganizationUnitName());
        builder.addRDN(BCStyle.C, info.getCountryCode());
        builder.addRDN(BCStyle.E, info.getEmail());
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, accountId.toString());

        Account newAccount = new Account(accountId, info.getEmail(), info.getPassword(),
                kp.getPrivate(), kp.getPublic(), builder.build(),
                new ArrayList<>());

        return _accountRepository.save(newAccount);
    }
}
