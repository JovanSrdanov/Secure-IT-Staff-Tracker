package pkibackend.pkibackend.service.implementations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pkibackend.pkibackend.Utilities.Keys;
import pkibackend.pkibackend.Utilities.ObjectMapperUtils;
import pkibackend.pkibackend.Utilities.PasswordGenerator;
import pkibackend.pkibackend.Utilities.UniqueFIleCreator;
import pkibackend.pkibackend.certificates.CertificateGenerator;
import pkibackend.pkibackend.dto.CertificateEntityInfoDto;
import pkibackend.pkibackend.dto.CertificateInfoDto;
import pkibackend.pkibackend.dto.CreateCertificateInfo;
import pkibackend.pkibackend.dto.EntityInfo;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.exceptions.InternalServerErrorException;
import pkibackend.pkibackend.exceptions.NotFoundException;
import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.model.*;
import pkibackend.pkibackend.repository.CertificateRepository;
import pkibackend.pkibackend.repository.KeystoreRowInfoRepository;
import pkibackend.pkibackend.repository.OcspTableRepository;
import pkibackend.pkibackend.service.interfaces.IAccountService;
import pkibackend.pkibackend.service.interfaces.ICertificateService;
import pkibackend.pkibackend.service.interfaces.IRoleService;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
@Primary
public class CertificateService implements ICertificateService {
    private static final Logger logger = LogManager.getLogger(CertificateService.class);
    private final CertificateRepository _certificateRepository;
    private final IAccountService _accountService;
    private final KeystoreRowInfoRepository _keystoreRowInfoRepository;
    private final OcspTableRepository _ocspTableRepository;
    private final IRoleService _roleService;
    private final JavaMailSender _javaMailSender;

    private final PasswordEncoder _passwordEncoder;


    @Autowired
    public CertificateService(CertificateRepository certificateRepository, AccountService accountService,
                              KeystoreRowInfoRepository keystoreRowInfoRepository,
                              OcspTableRepository ocspTableRepository, IRoleService roleService, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender) {
        _certificateRepository = certificateRepository;
        _accountService = accountService;
        _keystoreRowInfoRepository = keystoreRowInfoRepository;
        _ocspTableRepository = ocspTableRepository;
        _roleService = roleService;
        _passwordEncoder = passwordEncoder;
        _javaMailSender = javaMailSender;

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
    public Iterable<CertificateInfoDto> findAllAdmin() throws BadRequestException {
        List<KeystoreRowInfo> keystoreRowInfos = _keystoreRowInfoRepository.findAll();
        return findAllFromRowsWithCondition(keystoreRowInfos, false, false);
    }

    @Override
    public Iterable<CertificateInfoDto> findAllCaAdmin() throws BadRequestException {
        List<KeystoreRowInfo> keystoreRowInfos = _keystoreRowInfoRepository.findAll();
        return findAllFromRowsWithCondition(keystoreRowInfos, true, true);
    }

    @Override
    public Iterable<CertificateInfoDto> findAllForLoggedIn(UUID accountId) throws BadRequestException, NotFoundException {
        Account account = _accountService.findById(accountId);
        List<KeystoreRowInfo> keystoreRowInfos = new ArrayList<>(account.getKeyStoreRowsInfo());
        return findAllFromRowsWithCondition(keystoreRowInfos, false, false);
    }

    @Override
    public Iterable<CertificateInfoDto> findAllValidCaForLoggedIn(UUID accountId) throws BadRequestException, NotFoundException {
        Account account = _accountService.findById(accountId);
        List<KeystoreRowInfo> keystoreRowInfos = new ArrayList<>(account.getKeyStoreRowsInfo());
        return findAllFromRowsWithCondition(keystoreRowInfos, true, true);
    }

    private Iterable<CertificateInfoDto> findAllFromRowsWithCondition(List<KeystoreRowInfo> keystoreRowInfos,
                                                                      boolean shouldBeCa, boolean sholudBeNotRevokedAndExpired) throws BadRequestException {
        List<CertificateInfoDto> certificateDtos = new ArrayList<>();
        for (KeystoreRowInfo row : keystoreRowInfos) {
            var test = _certificateRepository.GetCertificate(row.getAlias(), row.getKeystoreName(), row.getPassword());
            Certificate certificate = new Certificate(test);
            certificate.setIssuerSerialNumber(row.getIssuingCertificateSerialNumber());
            if (shouldBeCa && !certificate.isCa()) {
                continue;
            }
            if (sholudBeNotRevokedAndExpired && !isChainValid(certificate.getSerialNumber())) {
                continue;
            }

            makeCertificateDto(certificateDtos, row, certificate);
        }
        return certificateDtos;

    }

    private void makeCertificateDto(List<CertificateInfoDto> certificateDtos, KeystoreRowInfo row, Certificate certificate) throws BadRequestException {
        CertificateInfoDto dto = ObjectMapperUtils.map(certificate, CertificateInfoDto.class);

        X500Name subjectInfo = new X500Name(certificate.getX509Certificate().getSubjectX500Principal().getName());
        X500Name issuerInfo = new X500Name(certificate.getX509Certificate().getIssuerX500Principal().getName());

        List<String> criticalExtensions = new ArrayList<>(certificate.getX509Certificate().getCriticalExtensionOIDs());
        List<String> nonCriticalExtensions = new ArrayList<>(certificate.getX509Certificate().getNonCriticalExtensionOIDs());
        List<String> extensions = new ArrayList<>();
        extensions.addAll(criticalExtensions);
        extensions.addAll(nonCriticalExtensions);

        dto.setSubjectInfo(new CertificateEntityInfoDto(subjectInfo, extensions));
        dto.setIssuerInfo(new CertificateEntityInfoDto(issuerInfo, extensions));
        dto.setRevoked(isRevoked(certificate.getSerialNumber()));
        dto.setAlias(row.getAlias());

        certificateDtos.add(dto);
    }

    @Override
    public Certificate findById(UUID id) {
        return null;
    }

    @Override
    public X509Certificate GetCertificateBySerialNumber(BigInteger serialNumber) throws BadRequestException {
        Optional<KeystoreRowInfo> issuerKeystoreInfo = _keystoreRowInfoRepository.findByCertificateSerialNumber(serialNumber);
        if (issuerKeystoreInfo.isEmpty()) {
            throw new BadRequestException("Certificate not found");
        }
        String keystoreName = issuerKeystoreInfo.get().getKeystoreName();

        String keyStorePassword = issuerKeystoreInfo.get().getPassword();

        return (X509Certificate) _certificateRepository.GetCertificateBySerialNumber(keystoreName, keyStorePassword, serialNumber);
    }

    @Override
    public Certificate save(Certificate entity) throws BadRequestException {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public Certificate generateCertificate(CreateCertificateInfo info)
            throws RuntimeException, BadRequestException, CertificateEncodingException, InternalServerErrorException, NotFoundException {

        Account issuer = new Account();
        Account subject = new Account();
        String newCertificateAlias = UUID.randomUUID().toString();
        Certificate newCertificate = new Certificate();
        BigInteger serialNumber = new BigInteger(32, new SecureRandom());
        String keystoreName;
        String keyStorePassword;

        // provera dal je self-signed, ako jeste serijski broj sertifikata iznad u lancu je null recimo
        // u suprotnom serijski broj se moze dobiti iz ekstenzije sertifikata
        if (info.getIssuingCertificateSerialNumber() == null) {
            if (_accountService.existsByEmail(info.getSubjectInfo().getEmail())) {
                throw new BadRequestException("Cannot issue a self-signed certificate, the subject " +
                        "already exists");
            }

            info.setIssuingCertificateSerialNumber(serialNumber);
            keystoreName = newCertificateAlias + "_keystore.p12";
            //It is being encrypted inside constructor
            keyStorePassword = PasswordGenerator.generatePassword(15);

            // TEST
            //createTextFile(keyStorePassword, "keystorePassword.txt");

            issuer = buildSelfSignedIssuer(info.getSubjectInfo(), newCertificate);
            subject = issuer;
        } else {
            String issuerId = getIssuerIdFromCertificate(info);
            if (issuerId == null) {
                throw new BadRequestException("Invalid issuer certificate serial number");
            }

            if (isIssuerSameAsSubject(info, issuerId)) {
                throw new BadRequestException("Non-root users cannot create self-signed certificates");
            }

//            if (!isNewCertificateDateValid(info.getStartDate(), info.getEndDate(), info.getIssuingCertificateSerialNumber())) {
//                throw new BadRequestException("Invalid date: new certificate must have an expiration date that" +
//                        "is before the issuing certificates expiration date");
//            }


            Optional<KeystoreRowInfo> issuerKeystoreInfo = _keystoreRowInfoRepository.findByCertificateSerialNumber(info.getIssuingCertificateSerialNumber());
            if (issuerKeystoreInfo.isEmpty()) {
                throw new BadRequestException("Issuer not found");
            }
            keystoreName = issuerKeystoreInfo.get().getKeystoreName();

            keyStorePassword = issuerKeystoreInfo.get().getPassword();

            if (isEndEntity(info.getIssuingCertificateSerialNumber())) {
                throw new BadRequestException("End entities cannot issue new certificates");
            }

            issuer = buildIssuer(UUID.fromString(issuerId), info.getIssuingCertificateSerialNumber(), newCertificate, keystoreName, keyStorePassword);
            subject = buildSubject(info.getSubjectInfo(), newCertificate, keystoreName, keyStorePassword);
        }


        X509Certificate certificate = CertificateGenerator.generateCertificate(newCertificate,
                info.getStartDate(), info.getEndDate(), serialNumber, info.getExtensions(),
                info.getIssuingCertificateSerialNumber());

        newCertificate.setSerialNumber(serialNumber);
        newCertificate.setStartDate(info.getStartDate());
        newCertificate.setEndDate(info.getEndDate());
        newCertificate.setX509Certificate(certificate);

        String keystoreRowInfoPassword = PasswordGenerator.generatePassword(15);
        // TEST
        //createTextFile(keystoreRowInfoPassword, "rowPassword.txt");

        KeystoreRowInfo rowInfo = new KeystoreRowInfo
                (UUID.randomUUID(), keystoreName, keyStorePassword, serialNumber, info.getIssuingCertificateSerialNumber(), newCertificateAlias, keystoreRowInfoPassword);

        subject.getKeyStoreRowsInfo().add(rowInfo);

        if (info.getSubjectInfo().getIsAccountNew()) {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            byte[] password = new byte[16];
            random.nextBytes(password);

            List<Role> roles = _roleService.findByName("ROLE_CERTIFICATE_USER_CHANGE_PASSWORD");
            subject.setRoles(roles);
            subject.setSalt(salt.toString());
            subject.setPassword(_passwordEncoder.encode(password.toString() + salt.toString()));
            _accountService.save(subject);
            //  sendEmailWithPassword(subject.getEmail(), password.toString());

        } else {
            _accountService.updateAccount(subject, subject.getId());
        }

        _keystoreRowInfoRepository.save(rowInfo);

        _certificateRepository.SaveCertificate(
                keystoreName,
                keyStorePassword,
                newCertificate,
                newCertificateAlias,
                rowInfo.getRowPassword());

        return newCertificate;
    }

    private void createTextFile(String content, String filename) {
        File file = UniqueFIleCreator.createUniqueFile(filename);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write("keystore password: " + content);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void sendEmailWithPassword(String email, String password) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setFrom("PKI_BUSEP");
        mail.setSubject("Protect your account");
        mail.setText("Your password is: " + password);
        _javaMailSender.send(mail);

    }

    private boolean isEndEntity(BigInteger issuingCertificateSerialNumber) throws CertificateEncodingException, BadRequestException {
        Optional<KeystoreRowInfo> issuerKeystoreInfo = _keystoreRowInfoRepository.findByCertificateSerialNumber(issuingCertificateSerialNumber);
        if (issuerKeystoreInfo.isEmpty()) {
            throw new BadRequestException("Certificate not found");
        }
        String keystoreName = issuerKeystoreInfo.get().getKeystoreName();

        String keyStorePassword = issuerKeystoreInfo.get().getPassword();

        X509Certificate certificate =
                (X509Certificate) _certificateRepository.GetCertificateBySerialNumber(keystoreName, keyStorePassword, issuingCertificateSerialNumber);
        X509CertificateHolder certificateHolder = new JcaX509CertificateHolder(certificate);

        Extension basicConstraintsExtension = certificateHolder.getExtension(Extension.basicConstraints);
        BasicConstraints basicConstraints = BasicConstraints.getInstance(basicConstraintsExtension.getParsedValue());
        return !basicConstraints.isCA();
    }

    private boolean isIssuerSameAsSubject(CreateCertificateInfo info, String issuerId) throws NotFoundException {
        Account issuer = _accountService.findById(UUID.fromString(issuerId));
        return info.getSubjectInfo().getEmail().equals(issuer.getEmail());
    }

    private String getIssuerIdFromCertificate(CreateCertificateInfo info) throws InternalServerErrorException, BadRequestException {
        Optional<KeystoreRowInfo> issuerKeystoreInfo = _keystoreRowInfoRepository.findByCertificateSerialNumber(info.getIssuingCertificateSerialNumber());
        if (issuerKeystoreInfo.isEmpty()) {
            throw new BadRequestException("Certificate not found");
        }
        String keystoreName = issuerKeystoreInfo.get().getKeystoreName();

        String keyStorePassword = issuerKeystoreInfo.get().getPassword();


        X509Certificate certificate =
                (X509Certificate) _certificateRepository.GetCertificateBySerialNumber(keystoreName, keyStorePassword, info.getIssuingCertificateSerialNumber());
        // uopste nije pronadjen sertifikat sa trazenim serijskim brojem
        if (certificate == null) {
            return null;
        }

        X500Principal issuerInfo = certificate.getSubjectX500Principal();

        String issuerUID = getUIDValueFromX500Principal(issuerInfo);

        if (issuerUID == null) {
            throw new InternalServerErrorException("Error while trying to get issuer information" +
                    "from the certificate");
        }
        logger.info("Issuer UID from certificate: {}", issuerUID);

        return issuerUID;
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

    private boolean isNewCertificateDateValid(Date startDate, Date endDate, BigInteger issuingCertificateSerialNumber) throws BadRequestException {
        Optional<KeystoreRowInfo> issuerKeystoreInfo = _keystoreRowInfoRepository.findByCertificateSerialNumber(issuingCertificateSerialNumber);
        if (issuerKeystoreInfo.isEmpty()) {
            throw new BadRequestException("Certificate not found");
        }
        String keystoreName = issuerKeystoreInfo.get().getKeystoreName();

        String keyStorePassword = issuerKeystoreInfo.get().getPassword();


        X509Certificate issuingCertificate = (X509Certificate) _certificateRepository.GetCertificateBySerialNumber(keystoreName, keyStorePassword, issuingCertificateSerialNumber);
        Date issuingStartDate = issuingCertificate.getNotBefore();
        Date issuingEndDate = issuingCertificate.getNotAfter();
        return (startDate.after(issuingStartDate) && endDate.before(issuingEndDate));
    }

    private Account buildSelfSignedIssuer(EntityInfo subjectInfo, Certificate newCertificate) {
        KeyPair kp = Keys.generateKeyPair();
        newCertificate.setSubjectPublicKey(kp.getPublic());
        newCertificate.setSubjectPrivateKey(kp.getPrivate());
        // TEST
        //File keyFile = UniqueFIleCreator.createUniqueFile("privateKeyTEST.key");

//        FileOutputStream fileOutputStream = null;
//        try {
//            fileOutputStream = new FileOutputStream(keyFile);
//            PEMWriter pemWriter = new PEMWriter(new OutputStreamWriter(fileOutputStream));
//            pemWriter.writeObject(newCertificate.getSubjectPrivateKey());
//
//            pemWriter.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        newCertificate.setIssuerPublicKey(newCertificate.getSubjectPublicKey());
        newCertificate.setIssuerPrivateKey(newCertificate.getSubjectPrivateKey());

        Account newIssuer = createNewCertificateEntity(subjectInfo, newCertificate);
        newCertificate.setIssuerInfo(newCertificate.getSubjectInfo());

        return newIssuer;
    }

    private Account buildSubject(EntityInfo info, Certificate newCertificate, String keyStoreName, String keyStorePassword) throws BadRequestException, CertificateEncodingException, NotFoundException {
        KeyPair kp = Keys.generateKeyPair();
        newCertificate.setSubjectPublicKey(kp.getPublic());
        newCertificate.setSubjectPrivateKey(kp.getPrivate());

        Boolean isAccountNew = info.getIsAccountNew();
        if (isAccountNew == null || !isAccountNew) {
            logger.info("Building existing subject...");

            Account account = _accountService.findByEmail(info.getEmail());
//            Set<KeystoreRowInfo> accountKeystoreInfo = account.getKeyStoreRowsInfo();
//            if (accountKeystoreInfo.isEmpty()) {
//                throw new BadRequestException("The subject already exists but does not have a certificate");
//            }
//
//            String alias = accountKeystoreInfo.stream().findAny().get().getAlias();
//            java.security.cert.Certificate certificate = _certificateRepository.GetCertificate(alias, keyStoreName, keyStorePassword);
//            X500Name subjectName = new JcaX509CertificateHolder((X509Certificate) certificate).getSubject();
//            logger.info("Existing subject info: {}", subjectName);
            //Ne ovo:
            //newCertificate.setSubjectInfo(subjectName);
            //Nego:
            X500NameBuilder builder = setupBasicCertificateInfo(info, account.getId());
            newCertificate.setSubjectInfo(builder.build());

            return account;
        }
        logger.info("Building a new subject...");
        return createNewCertificateEntity(info, newCertificate);
    }

    private Account createNewCertificateEntity(EntityInfo info, Certificate newCertificate) {
        UUID accountId = UUID.randomUUID();
        X500NameBuilder builder = setupBasicCertificateInfo(info, accountId);
        newCertificate.setSubjectInfo(builder.build());
        List<Role> roles = _roleService.findByName("ROLE_CERTIFICATE_USER_CHANGE_PASSWORD");
        return new Account(accountId, info.getEmail(), "password", "salt", roles, new HashSet<>());
    }

    private Account buildIssuer(UUID issuerId, BigInteger serialNumber, Certificate newCertificate, String keyStoreName, String keyStorePassword)
            throws BadRequestException, CertificateEncodingException, NotFoundException {
        String issuerCertificateAlias = _certificateRepository.GetCertificateAliasBySerialNumber(keyStoreName, keyStorePassword, serialNumber);

        if (issuerCertificateAlias == null) {
            throw new BadRequestException("Issuer with given serial number not found");
        }

        Optional<KeystoreRowInfo> issuerCertificatesInfo = _keystoreRowInfoRepository.findByAlias(issuerCertificateAlias);
        KeystoreRowInfo rowInfo = null;
        if (issuerCertificatesInfo.isPresent()) {
            rowInfo = issuerCertificatesInfo.get();
        } else {
            throw new BadRequestException("The user does not exist");
        }

        PrivateKey issuerPrivateKey = _certificateRepository.GetCertificatePrivateKey(keyStoreName,
                keyStorePassword, issuerCertificateAlias, rowInfo.getRowPassword()
        );
        PublicKey issuerPublicKey =
                _certificateRepository.GetCertificate(issuerCertificateAlias, keyStoreName, keyStorePassword).getPublicKey();

        Account account = _accountService.findById(issuerId);

        java.security.cert.Certificate certificate = _certificateRepository.GetCertificate(issuerCertificateAlias, keyStoreName, keyStorePassword);
        X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) certificate).getSubject();
        logger.info("Issuer info: {}", issuerName);

        newCertificate.setIssuerPublicKey(issuerPublicKey);
        newCertificate.setIssuerPrivateKey(issuerPrivateKey);
        newCertificate.setIssuerInfo(issuerName);
        return account;
    }

    private X500NameBuilder setupBasicCertificateInfo(EntityInfo info, UUID accountId) {
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
        return builder;
    }

    public void revoke(BigInteger certSerialNum) throws BadRequestException {
        Optional<KeystoreRowInfo> issuerKeystoreInfo = _keystoreRowInfoRepository.findByCertificateSerialNumber(certSerialNum);
        if (issuerKeystoreInfo.isEmpty()) {
            throw new BadRequestException("Issuer not found");
        }
        String keystoreName = issuerKeystoreInfo.get().getKeystoreName();

        String keyStorePassword = issuerKeystoreInfo.get().getPassword();


        //Retrieve certificate
        java.security.cert.Certificate rawCertificate = _certificateRepository.GetCertificateBySerialNumber(keystoreName, keyStorePassword, certSerialNum);
        Certificate certificate = new Certificate(rawCertificate);
        certificate.setIssuerSerialNumber(issuerKeystoreInfo.get().getCertificateSerialNumber());

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
            Iterable<Certificate> children = _certificateRepository.GetChildren(keystoreName, keyStorePassword, certificate.getSerialNumber());

            for (Certificate child : children) {
                revoke(child.getSerialNumber());
            }
        }
    }

    public boolean isRevoked(BigInteger certSerialNum) throws BadRequestException {
        Optional<KeystoreRowInfo> issuerKeystoreInfo = _keystoreRowInfoRepository.findByCertificateSerialNumber(certSerialNum);
        if (issuerKeystoreInfo.isEmpty()) {
            throw new BadRequestException("Certificate not found");
        }
        String keystoreName = issuerKeystoreInfo.get().getKeystoreName();

        String keyStorePassword = issuerKeystoreInfo.get().getPassword();


        java.security.cert.Certificate rawCertificate = _certificateRepository.GetCertificateBySerialNumber(keystoreName, keyStorePassword, certSerialNum);
        Certificate certificate = new Certificate(rawCertificate);
        certificate.setIssuerSerialNumber(issuerKeystoreInfo.get().getCertificateSerialNumber());

        java.security.cert.Certificate issuerRawCertificate = _certificateRepository.GetCertificateBySerialNumber(keystoreName, keyStorePassword, certificate.getIssuerSerialNumber());
        Certificate issuerCertificate = new Certificate(issuerRawCertificate);

        Optional<OcspTable> result = _ocspTableRepository.findByCaSerialNumber(issuerCertificate.getSerialNumber());
        if (result.isEmpty()) {
            return false;
        }

        return result.get().getRevokedCertificateSerialNums().contains(certificate.getSerialNumber());
    }

    @Override
    public boolean isInKeystore(Set<KeystoreRowInfo> rows, BigInteger certSerialNum) {
        for (KeystoreRowInfo row : rows) {
            if (row.getCertificateSerialNumber().equals(certSerialNum)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PrivateKey GetCertificatePrivateKey(String keyStoreName, String keyStorePass, String alias, String pass) {
        return _certificateRepository.GetCertificatePrivateKey(keyStoreName, keyStorePass, alias, pass);
    }

    @Override
    public boolean isChainValid(BigInteger certSerialNum) throws BadRequestException {
        Optional<KeystoreRowInfo> issuerKeystoreInfo = _keystoreRowInfoRepository.findByCertificateSerialNumber(certSerialNum);
        if (issuerKeystoreInfo.isEmpty()) {
            throw new BadRequestException("Certificate not found");
        }
        String keystoreName = issuerKeystoreInfo.get().getKeystoreName();
        String keyStorePassword = issuerKeystoreInfo.get().getPassword();

        java.security.cert.Certificate rawCertificate = _certificateRepository.GetCertificateBySerialNumber(keystoreName, keyStorePassword, certSerialNum);
        Certificate certificate = new Certificate(rawCertificate);
        certificate.setIssuerSerialNumber(issuerKeystoreInfo.get().getCertificateSerialNumber());

        if (isExpired(certificate)) return false;

        if (!isSignatureValid(certificate, keystoreName, keyStorePassword)) return false;

        if (isRevoked(certSerialNum)) return false;

        if (Objects.equals(certificate.getSerialNumber(), certificate.getIssuerSerialNumber())) {
            return true;
        } else {
            return isChainValid(certificate.getIssuerSerialNumber());
        }
    }

    private boolean isSignatureValid(Certificate certificate, String keyStoreName, String keyStorePassword) {
        PublicKey issuerPublicKey = null;

        if (Objects.equals(certificate.getSerialNumber(), certificate.getIssuerSerialNumber())) {
            issuerPublicKey = certificate.getSubjectPublicKey();
        } else {
            java.security.cert.Certificate issuerRawCertificate = _certificateRepository.GetCertificateBySerialNumber(keyStoreName, keyStorePassword, certificate.getIssuerSerialNumber());
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

    private boolean isExpired(Certificate certificate) {
        Date today = new Date();
        return !certificate.getEndDate().after(today) && !certificate.getStartDate().before(today);
    }
}
