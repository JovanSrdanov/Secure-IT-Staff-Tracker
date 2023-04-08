package pkibackend.pkibackend.service.implementations;

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
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.model.KeystoreRowInfo;
import pkibackend.pkibackend.repository.AccountRepository;
import pkibackend.pkibackend.repository.CertificateRepository;
import pkibackend.pkibackend.repository.KeystoreRowInfoRepository;
import pkibackend.pkibackend.service.interfaces.ICertificateService;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.UUID;

@Service
@Primary
public class CertificateService implements ICertificateService {
    private final CertificateRepository _certificateRepository;
    private final AccountRepository _accountRepository;
    private final KeystoreRowInfoRepository _keystoreRowInfoRepository;

    @Value("${keystorePassword}")
    private String keyStorePassword;

    @Autowired
    public CertificateService(CertificateRepository certificateRepository, AccountRepository accountRepository, KeystoreRowInfoRepository keystoreRowInfoRepository) {
        _certificateRepository = certificateRepository;
        _accountRepository = accountRepository;
        _keystoreRowInfoRepository = keystoreRowInfoRepository;
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
        Account issuer = new Account();
        Account subject = new Account();
        Certificate newCertificate = new Certificate();

        // provera dal je self-signed, ako jeste serijski broj sertifikata iznad u lancu je null recimo
        // u suprotnom serijski broj se moze dobiti iz ekstenzije sertifikata
        if (info.getIssuingCertificateSerialNumber() == null) {
            issuer = buildSubject(info.getIssuerInfo(), newCertificate);
            subject = issuer;
            newCertificate.setIssuerPublicKey(newCertificate.getSubjectPublicKey());
            newCertificate.setIssuerPrivateKey(newCertificate.getSubjectPrivateKey());
            newCertificate.setIssuerInfo(newCertificate.getSubjectInfo());
        }
        else {
            issuer = buildIssuer(info.getIssuerInfo(), info.getIssuingCertificateSerialNumber(), newCertificate);
            subject = buildSubject(info.getSubjectInfo(), newCertificate);
        }

        BigInteger serialNumber = new BigInteger(32, new SecureRandom());

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
        return new Account(accountId, info.getEmail(), info.getPassword(), new HashSet<>());
    }

    private Account buildIssuer(EntityInfo info, BigInteger serialNumber, Certificate newCertificate) {
        String issuerCertificateAlias = _certificateRepository.GetCertificateBySerialNumber
                (keyStorePassword, serialNumber);
        KeystoreRowInfo rowInfo = _keystoreRowInfoRepository.findByAlias(issuerCertificateAlias).get();

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
}
