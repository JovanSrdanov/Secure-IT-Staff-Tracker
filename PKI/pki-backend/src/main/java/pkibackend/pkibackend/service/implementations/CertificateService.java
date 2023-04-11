package pkibackend.pkibackend.service.implementations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pkibackend.pkibackend.Utilities.Keys;
import pkibackend.pkibackend.Utilities.PasswordGenerator;
import pkibackend.pkibackend.certificates.CertificateGenerator;
import pkibackend.pkibackend.dto.CreateCertificateInfo;
import pkibackend.pkibackend.dto.EntityInfo;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.model.*;
import pkibackend.pkibackend.repository.AccountRepository;
import pkibackend.pkibackend.repository.CertificateRepository;
import pkibackend.pkibackend.repository.KeystoreRowInfoRepository;
import pkibackend.pkibackend.repository.OcspTableRepository;
import pkibackend.pkibackend.service.interfaces.ICertificateService;
import pkibackend.pkibackend.service.interfaces.IRoleService;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
public class CertificateService implements ICertificateService {
    private static final Logger logger = LogManager.getLogger(CertificateService.class);
    private final CertificateRepository _certificateRepository;
    private final AccountRepository _accountRepository;
    private final KeystoreRowInfoRepository _keystoreRowInfoRepository;
    private final OcspTableRepository _ocspTableRepository;
    private final IRoleService _roleService;
    @Value("${keystorePassword}")
    private String keyStorePassword;

    @Autowired
    public CertificateService(CertificateRepository certificateRepository, AccountRepository accountRepository,
                              KeystoreRowInfoRepository keystoreRowInfoRepository,
                              OcspTableRepository ocspTableRepository, IRoleService roleService) {
        _certificateRepository = certificateRepository;
        _accountRepository = accountRepository;
        _keystoreRowInfoRepository = keystoreRowInfoRepository;
        _ocspTableRepository = ocspTableRepository;
        _roleService = roleService;
    }

    private static X500NameBuilder setupBasicCertificateInfo(EntityInfo info) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, info.getCommonName());
        builder.addRDN(BCStyle.SURNAME, info.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, info.getGivenName());
        builder.addRDN(BCStyle.O, info.getOrganization());
        builder.addRDN(BCStyle.OU, info.getOrganizationUnitName());
        builder.addRDN(BCStyle.C, info.getCountryCode());
        builder.addRDN(BCStyle.E, info.getEmail());
        return builder;
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
    public Certificate generateCertificate(CreateCertificateInfo info) throws RuntimeException, BadRequestException {
        Account issuer = new Account();
        Account subject = new Account();
        Certificate newCertificate = new Certificate();

        // provera dal je self-signed, ako jeste serijski broj sertifikata iznad u lancu je null recimo
        // u suprotnom serijski broj se moze dobiti iz ekstenzije sertifikata
        //TODO staviti da je obaavezno da se ima issuer serial number kao extenzija
        BigInteger serialNumber = new BigInteger(32, new SecureRandom());
        if (info.getIssuingCertificateSerialNumber() == null) {
            info.setIssuingCertificateSerialNumber(serialNumber);
            issuer = buildSubject(info.getIssuerInfo(), newCertificate);
            subject = issuer;
            newCertificate.setIssuerPublicKey(newCertificate.getSubjectPublicKey());
            newCertificate.setIssuerPrivateKey(newCertificate.getSubjectPrivateKey());
            newCertificate.setIssuerInfo(newCertificate.getSubjectInfo());
        } else {
            issuer = buildIssuer(info.getIssuerInfo(), info.getIssuingCertificateSerialNumber(), newCertificate);
            subject = buildSubject(info.getSubjectInfo(), newCertificate);
        }


        X509Certificate certificate = CertificateGenerator.generateCertificate(newCertificate,
                info.getStartDate(), info.getEndDate(), serialNumber, info.getExtensions(),
                info.getIssuingCertificateSerialNumber());

        newCertificate.setSerialNumber(serialNumber);
        newCertificate.setStartDate(info.getStartDate());
        newCertificate.setEndDate(info.getEndDate());
        newCertificate.setX509Certificate(certificate);

        KeystoreRowInfo rowInfo = new KeystoreRowInfo
                (UUID.randomUUID(), info.getAlias(), PasswordGenerator.generatePassword(15));
        _keystoreRowInfoRepository.save(rowInfo);

        subject.getKeyStoreRowsInfo().add(rowInfo);
        _accountRepository.save(subject);
        if (!subject.getEmail().equals(issuer.getEmail())) {
            _accountRepository.save(issuer);
        }

        _certificateRepository.SaveCertificate(
                newCertificate,
                keyStorePassword, info.getAlias(),
                rowInfo.getPassword());

        return newCertificate;
    }

    private Account buildSubject(EntityInfo info, Certificate newCertificate) {
        KeyPair kp = Keys.generateKeyPair();
        X500NameBuilder builder = setupBasicCertificateInfo(info);

        newCertificate.setSubjectPublicKey(kp.getPublic());
        newCertificate.setSubjectPrivateKey(kp.getPrivate());

        if (_accountRepository.findByEmail(info.getEmail()).isPresent()) {
            Account account = _accountRepository.findByEmail(info.getEmail()).get();
            builder.addRDN(BCStyle.UID, account.getId().toString());
            newCertificate.setSubjectInfo(builder.build());

            return account;
        }

        UUID accountId = UUID.randomUUID();
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, accountId.toString());
        newCertificate.setSubjectInfo(builder.build());
        List<Role> roles = _roleService.findByName("ROLE_CERTIFICATE_USER_CHANGE_PASSWORD");
        // Todo JOVAN ovde dodati lepo generisanje passworda i salta i mail
        return new Account(accountId, info.getEmail(), "password", "salt", roles, new HashSet<>());
    }

    private Account buildIssuer(EntityInfo info, BigInteger serialNumber, Certificate newCertificate) throws BadRequestException {
        String issuerCertificateAlias = _certificateRepository.GetCertificateAliasBySerialNumber(keyStorePassword, serialNumber);

        Optional<KeystoreRowInfo> result = _keystoreRowInfoRepository.findByAlias(issuerCertificateAlias);
        KeystoreRowInfo rowInfo = null;
        if (result.isPresent()) {
            rowInfo = result.get();
        } else {
            throw new BadRequestException("Unexisting issuer");
        }

        PrivateKey issuerPrivateKey = _certificateRepository.GetCertificatePrivateKey(
                keyStorePassword, issuerCertificateAlias, rowInfo.getPassword()
        );
        PublicKey issuerPublicKey =
                _certificateRepository.GetCertificate(issuerCertificateAlias, keyStorePassword).getPublicKey();

        X500NameBuilder builder = setupBasicCertificateInfo(info);
        Account account = _accountRepository.findByEmail(info.getEmail()).get();
        builder.addRDN(BCStyle.UID, account.getId().toString());

        newCertificate.setIssuerPublicKey(issuerPublicKey);
        newCertificate.setIssuerPrivateKey(issuerPrivateKey);
        newCertificate.setIssuerInfo(builder.build());
        return account;
    }

    public void revoke(BigInteger certSerialNum) {
        //Retrieve certificate
        java.security.cert.Certificate rawCertificate = _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, certSerialNum);
        Certificate certificate = new Certificate(rawCertificate);

        //Get issuers ocsp
        Optional<OcspTable> queryResult = _ocspTableRepository.findByCaSerialNumber(certificate.getIssuerSerialNumber());
        OcspTable issuerOcsp;
        //Add certificate serial number to issuers ocsp
        if (queryResult.isEmpty()) {
            issuerOcsp = new OcspTable(UUID.randomUUID(), certificate.getIssuerSerialNumber(), new HashSet<>());
            issuerOcsp.getRevokedCertificateSerialNums().add(certificate.getSerialNumber());
        } else {
            issuerOcsp = queryResult.get();
            if (issuerOcsp.getRevokedCertificateSerialNums().contains(certificate.getSerialNumber())) {
                //Also it means all of its children are also already revoked
                return;
            }
            issuerOcsp.getRevokedCertificateSerialNums().add(certificate.getSerialNumber());
        }

        _ocspTableRepository.save(issuerOcsp);
        logger.info("Revoked certificate: {}", certSerialNum);

        if (certificate.isCa()) {
            Iterable<Certificate> children = _certificateRepository.
                    GetChildren("src/main/resources/static/example.jks", keyStorePassword, certificate.getSerialNumber());

            for (Certificate child : children) {
                revoke(child.getSerialNumber());
            }
        }
    }

    public boolean isRevoked(BigInteger certSerialNum) {
        java.security.cert.Certificate rawCertificate = _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, certSerialNum);
        Certificate certificate = new Certificate(rawCertificate);

        java.security.cert.Certificate issuerRawCertificate = _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, certificate.getIssuerSerialNumber());
        Certificate issuerCertificate = new Certificate(issuerRawCertificate);

        Optional<OcspTable> result = _ocspTableRepository.findByCaSerialNumber(issuerCertificate.getSerialNumber());
        if (result.isEmpty()) {
            return false;
        }

        return result.get().getRevokedCertificateSerialNums().contains(certificate.getSerialNumber());
    }
}
