package pkibackend.pkibackend.service.implementations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
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
import pkibackend.pkibackend.exceptions.InternalServerErrorException;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.model.KeystoreRowInfo;
import pkibackend.pkibackend.model.OcspTable;
import pkibackend.pkibackend.repository.CertificateRepository;
import pkibackend.pkibackend.repository.KeystoreRowInfoRepository;
import pkibackend.pkibackend.repository.OcspTableRepository;
import pkibackend.pkibackend.service.interfaces.IAccountService;
import pkibackend.pkibackend.service.interfaces.ICertificateService;

import javax.security.auth.x500.X500Principal;
import java.security.cert.CertificateException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
@Primary
public class CertificateService implements ICertificateService {
    private final CertificateRepository _certificateRepository;
    private final IAccountService _accountService;
    private final KeystoreRowInfoRepository _keystoreRowInfoRepository;
    private final OcspTableRepository _ocspTableRepository;
    private static final Logger logger = LogManager.getLogger(CertificateService.class);

    @Value("${keystorePassword}")
    private String keyStorePassword;

    @Autowired
    public CertificateService(CertificateRepository certificateRepository, AccountService accountService,
                              KeystoreRowInfoRepository keystoreRowInfoRepository,
                              OcspTableRepository ocspTableRepository) {
        _certificateRepository = certificateRepository;
        _accountService = accountService;
        _keystoreRowInfoRepository = keystoreRowInfoRepository;
        _ocspTableRepository = ocspTableRepository;
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
    public X509Certificate GetCertificateBySerialNumber(BigInteger serialNumber) {
        return (X509Certificate) _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, serialNumber);
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
    public Certificate generateCertificate(CreateCertificateInfo info)
            throws RuntimeException, BadRequestException, CertificateEncodingException, InternalServerErrorException {
        Account issuer = new Account();
        Account subject = new Account();
        Certificate newCertificate = new Certificate();
        BigInteger serialNumber = new BigInteger(32, new SecureRandom());

        // provera dal je self-signed, ako jeste serijski broj sertifikata iznad u lancu je null recimo
        // u suprotnom serijski broj se moze dobiti iz ekstenzije sertifikata
        //TODO staviti da je obavezno da se ima issuer serial number kao extenzija
        if (info.getIssuingCertificateSerialNumber() == null) {
            info.setIssuingCertificateSerialNumber(serialNumber);
            issuer = buildSubject(info.getIssuerInfo(), newCertificate);
            subject = issuer;
            newCertificate.setIssuerPublicKey(newCertificate.getSubjectPublicKey());
            newCertificate.setIssuerPrivateKey(newCertificate.getSubjectPrivateKey());
            newCertificate.setIssuerInfo(newCertificate.getSubjectInfo());
        }
        else {
            if (info.getSubjectInfo().getEmail().equals(info.getIssuerInfo().getEmail())) {
                throw new BadRequestException("Non-root users cannot create self-signed certificates");
            }

            if (!isIssuerSerialNumberValid(info)) {
                throw new BadRequestException("The provided issuer serial number does" +
                        "not match the issuer");
            }

            if (!isNewCertificateDateValid(info.getStartDate(), info.getEndDate(), info.getIssuingCertificateSerialNumber())) {
                throw new BadRequestException("Invalid date: new certificate must have an expiration date that" +
                        "is before the issuing certificates expiration date");
            }

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

        if (info.getSubjectInfo().getIsAccountNew()) {
            _accountService.save(subject);
        }
        else {
            _accountService.updateAccount(subject, subject.getId());
        }

        if (!subject.getEmail().equals(issuer.getEmail())) {
            _accountService.save(issuer);
        }

        _certificateRepository.SaveCertificate(
                newCertificate,
                keyStorePassword, info.getAlias(),
                rowInfo.getPassword());

        return newCertificate;
    }

    private boolean isIssuerSerialNumberValid(CreateCertificateInfo info) throws InternalServerErrorException {
        X509Certificate certificate =
                (X509Certificate) _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, info.getIssuingCertificateSerialNumber());
        // uopste nije pronadjen sertifikat sa trazenim serijskim brojem
        if (certificate == null) {
            return false;
        }

        X500Principal issuerInfo = certificate.getSubjectX500Principal();

        // TODO Stefan: promeni da se issuer dobija iz dto-a preko id-a
        Account issuer = _accountService.findByEmail(info.getIssuerInfo().getEmail());
        String issuerUID = getUIDValueFromX500Principal(issuerInfo);

        if (issuerUID == null) {
            throw new InternalServerErrorException("Error while trying to get issuer information" +
                    "from the certificate");
        }
        System.out.println("Issuer UID from certificate: " + issuerUID);

        return issuer.getId().equals(UUID.fromString(issuerUID));
    }

    private String getUIDValueFromX500Principal(X500Principal issuerInfo) {
        String[] attributeValues = issuerInfo.getName().split(",");
        for (String attribute : attributeValues) {
            String[] attributeParts = attribute.split("=");
            if (attributeParts.length == 2 && attributeParts[0].trim().equals("UID")) {
                return attributeParts[1].trim();
            }
        }
        return null;
    }

    private boolean isNewCertificateDateValid(Date startDate, Date endDate, BigInteger issuingCertificateSerialNumber) {
        X509Certificate issuingCertificate = (X509Certificate) _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, issuingCertificateSerialNumber);
        Date issuingStartDate = issuingCertificate.getNotBefore();
        Date issuingEndDate = issuingCertificate.getNotAfter();
        return (startDate.after(issuingStartDate) && endDate.before(issuingEndDate));
    }

    private Account buildSubject(EntityInfo info, Certificate newCertificate) throws BadRequestException, CertificateEncodingException {
        KeyPair kp = Keys.generateKeyPair();
        newCertificate.setSubjectPublicKey(kp.getPublic());
        newCertificate.setSubjectPrivateKey(kp.getPrivate());

        Boolean isAccountNew = info.getIsAccountNew();
        if (isAccountNew == null || !isAccountNew) {
            System.out.println("Account not new");
            Account account = _accountService.findByEmail(info.getEmail());
            Set<KeystoreRowInfo> accountKeystoreInfo = account.getKeyStoreRowsInfo();
            if (accountKeystoreInfo.isEmpty()) {
                throw new BadRequestException("The subject already exists but does not have a certificate");
            }

            // TODO Jovan: evo ti jovane za accountove info (koji vec postoje)
            String alias = accountKeystoreInfo.stream().findAny().get().getAlias();
            java.security.cert.Certificate certificate = _certificateRepository.GetCertificate(alias, keyStorePassword);
            X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) certificate).getSubject();
            System.out.println("X500Name: " + issuerName);
            newCertificate.setSubjectInfo(issuerName);
            return account;
        }
        System.out.println("Account is new");
        X500NameBuilder builder = setupBasicCertificateInfo(info);
//        if (_accountRepository.findByEmail(info.getEmail()).isPresent()) {
//            Account account = _accountRepository.findByEmail(info.getEmail()).get();
//            builder.addRDN(BCStyle.UID, account.getId().toString());
//            newCertificate.setSubjectInfo(builder.build());
//
//            return account;
//        }

        UUID accountId = UUID.randomUUID();
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, accountId.toString());
        newCertificate.setSubjectInfo(builder.build());
        return new Account(accountId, info.getEmail(), info.getPassword(), new HashSet<>());
    }

    private Account buildIssuer(EntityInfo info, BigInteger serialNumber, Certificate newCertificate)
            throws BadRequestException {
        String issuerCertificateAlias = _certificateRepository.GetCertificateAliasBySerialNumber (keyStorePassword, serialNumber);

        if (issuerCertificateAlias == null) {
            throw new BadRequestException("Issuer with given serial number not found");
        }

        Optional<KeystoreRowInfo> result =  _keystoreRowInfoRepository.findByAlias(issuerCertificateAlias);
        KeystoreRowInfo rowInfo = null;
        if(result.isPresent()){
            rowInfo = result.get();
        }
        else
        {
            throw new BadRequestException("The user does not exist");
        }

        PrivateKey issuerPrivateKey = _certificateRepository.GetCertificatePrivateKey(
                keyStorePassword, issuerCertificateAlias, rowInfo.getPassword()
        );
        PublicKey issuerPublicKey =
                _certificateRepository.GetCertificate(issuerCertificateAlias, keyStorePassword).getPublicKey();

        X500NameBuilder builder = setupBasicCertificateInfo(info);
        Account account = _accountService.findByEmail(info.getEmail());
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

    public void revoke(BigInteger certSerialNum){
        //Retrieve certificate
        java.security.cert.Certificate rawCertificate = _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, certSerialNum);
        Certificate certificate = new Certificate(rawCertificate);

        //Get issuers ocsp
        Optional<OcspTable> queryResult = _ocspTableRepository.findByCaSerialNumber(certificate.getIssuerSerialNumber());
        OcspTable issuerOcsp;
        //Add certificate serial number to issuers ocsp
        if(queryResult.isEmpty()){
            issuerOcsp = new OcspTable(UUID.randomUUID(), certificate.getIssuerSerialNumber(), new HashSet<>());
            issuerOcsp.getRevokedCertificateSerialNums().add(certificate.getSerialNumber());
        }
        else
        {
            issuerOcsp = queryResult.get();
            if(issuerOcsp.getRevokedCertificateSerialNums().contains(certificate.getSerialNumber())){
                //Also it means all of its children are also already revoked
                return;
            }
            issuerOcsp.getRevokedCertificateSerialNums().add(certificate.getSerialNumber());
        }

        _ocspTableRepository.save(issuerOcsp);
        logger.info("Revoked certificate: {}", certSerialNum);

        if(certificate.isCa())
        {
            Iterable<Certificate> children = _certificateRepository.
                    GetChildren("src/main/resources/static/example.jks",keyStorePassword, certificate.getSerialNumber() );

            for(Certificate child : children)
            {
                revoke(child.getSerialNumber());
            }
        }
    }

    public boolean isRevoked(BigInteger certSerialNum){
        java.security.cert.Certificate rawCertificate = _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, certSerialNum);
        Certificate certificate = new Certificate(rawCertificate);

        java.security.cert.Certificate issuerRawCertificate = _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, certificate.getIssuerSerialNumber());
        Certificate issuerCertificate = new Certificate(issuerRawCertificate);

        Optional<OcspTable> result = _ocspTableRepository.findByCaSerialNumber(issuerCertificate.getSerialNumber());
        if(result.isEmpty()){
            return  false;
        }

        return result.get().getRevokedCertificateSerialNums().contains(certificate.getSerialNumber());
    }

    public boolean isChainValid(BigInteger certSerialNum) {
        java.security.cert.Certificate rawCertificate = _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, certSerialNum);
        Certificate certificate = new Certificate(rawCertificate);

        if(isExired(certificate)) return false;

        if (!isSignatureValid(certificate)) return false;

        if(isRevoked(certSerialNum)) return false;

        if(Objects.equals(certificate.getSerialNumber(), certificate.getIssuerSerialNumber())) {
            return true;
        }
        else {
            return isChainValid(certificate.getIssuerSerialNumber());
        }
    }

    private boolean isSignatureValid(Certificate certificate) {
        PublicKey issuerPublicKey = null;

        if(Objects.equals(certificate.getSerialNumber(), certificate.getIssuerSerialNumber())) {
            issuerPublicKey = certificate.getSubjectPublicKey();
        }
        else  {
            java.security.cert.Certificate issuerRawCertificate = _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, certificate.getIssuerSerialNumber());
            Certificate issuerCertificate = new Certificate(issuerRawCertificate);
            issuerPublicKey = issuerCertificate.getSubjectPublicKey();
        }

        try {
            certificate.getX509Certificate().verify(issuerPublicKey);
        } catch (CertificateException |
                 NoSuchAlgorithmException |
                 InvalidKeyException |
                 NoSuchProviderException |
                 SignatureException e) {
            //throw new RuntimeException(e);
            return false;
        }
        return true;
    }

    private boolean isExired(Certificate certificate) {
        Date today = new Date();
        if (certificate.getEndDate().after(today) || certificate.getStartDate().before(today)) {
            return false;
        }
        return true;
    }
}
