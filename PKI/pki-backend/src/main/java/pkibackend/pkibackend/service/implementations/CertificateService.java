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
import pkibackend.pkibackend.Utilities.ObjectMapperUtils;
import pkibackend.pkibackend.Utilities.PasswordGenerator;
import pkibackend.pkibackend.certificates.CertificateGenerator;
import pkibackend.pkibackend.dto.CertificateEntityInfoDto;
import pkibackend.pkibackend.dto.CertificateInfoDto;
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
        List<KeystoreRowInfo> keystoreRowInfos = _keystoreRowInfoRepository.findAll();
        List<Certificate> certificates = new ArrayList<>();
        for (KeystoreRowInfo row : keystoreRowInfos) {
            Certificate certificate = new Certificate(_certificateRepository.GetCertificate(row.getAlias(), keyStorePassword));
            certificates.add(certificate);
        }
        return certificates;
    }

    @Override
    public Iterable<CertificateInfoDto> findAllAdmin(){
        List<KeystoreRowInfo> keystoreRowInfos = _keystoreRowInfoRepository.findAll();
        List<CertificateInfoDto> certificateDtos = new ArrayList<>();
        for (KeystoreRowInfo row : keystoreRowInfos) {
            Certificate certificate = new Certificate(_certificateRepository.GetCertificate(row.getAlias(), keyStorePassword));
            CertificateInfoDto dto = ObjectMapperUtils.map(certificate, CertificateInfoDto.class);

            X500Name subjectInfo = new X500Name(certificate.getX509Certificate().getSubjectX500Principal().getName());
            X500Name issuerInfo = new X500Name(certificate.getX509Certificate().getIssuerX500Principal().getName());

            dto.setSubjectInfo(new CertificateEntityInfoDto(subjectInfo));
            dto.setIssuerInfo(new CertificateEntityInfoDto(issuerInfo));
            dto.setRevoked(isRevoked(certificate.getSerialNumber()));
            dto.setAlias(row.getAlias());

            certificateDtos.add(dto);
        }
        return certificateDtos;
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
        if (_certificateRepository.findAliasInKeystore(info.getAlias(), keyStorePassword)) {
            throw new BadRequestException("Given alias already exists in the keystore");
        }

        Account issuer = new Account();
        Account subject = new Account();
        Certificate newCertificate = new Certificate();
        BigInteger serialNumber = new BigInteger(32, new SecureRandom());

        // provera dal je self-signed, ako jeste serijski broj sertifikata iznad u lancu je null recimo
        // u suprotnom serijski broj se moze dobiti iz ekstenzije sertifikata
        //TODO staviti da je obavezno da se ima issuer serial number kao extenzija
        if (info.getIssuingCertificateSerialNumber() == null) {
            info.setIssuingCertificateSerialNumber(serialNumber);
            issuer = buildSelfSignedIssuer(info.getSubjectInfo(), newCertificate);
            subject = issuer;
        }
        else {
//            if (info.getIssuerId() == null) {
//                throw new BadRequestException("No issuer provided");
//            }
            String issuerId = getIssuerIdFromCertificate(info);
            if (issuerId == null) {
                throw new BadRequestException("Invalid issuer certificate serial number");
            }

            if (isSelfSignedCertificate(info, issuerId)) {
                throw new BadRequestException("Non-root users cannot create self-signed certificates");
            }

//            if (!getIssuerIdFromCertificate(info)) {
//                throw new BadRequestException("The provided issuer serial number does" +
//                        "not match the issuer");
//            }

            if (!isNewCertificateDateValid(info.getStartDate(), info.getEndDate(), info.getIssuingCertificateSerialNumber())) {
                throw new BadRequestException("Invalid date: new certificate must have an expiration date that" +
                        "is before the issuing certificates expiration date");
            }

            issuer = buildIssuer(UUID.fromString(issuerId), info.getIssuingCertificateSerialNumber(), newCertificate);
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

    private boolean isSelfSignedCertificate(CreateCertificateInfo info, String issuerId) {
        Account issuer = _accountService.findById(UUID.fromString(issuerId));
        return info.getSubjectInfo().getEmail().equals(issuer.getEmail());
    }

    private String getIssuerIdFromCertificate(CreateCertificateInfo info) throws InternalServerErrorException {
        X509Certificate certificate =
                (X509Certificate) _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, info.getIssuingCertificateSerialNumber());
        // uopste nije pronadjen sertifikat sa trazenim serijskim brojem
        if (certificate == null) {
            return null;
        }

        X500Principal issuerInfo = certificate.getSubjectX500Principal();

        //Account issuer = _accountService.findById(info.getIssuerId());
        String issuerUID = getUIDValueFromX500Principal(issuerInfo);

        if (issuerUID == null) {
            throw new InternalServerErrorException("Error while trying to get issuer information" +
                    "from the certificate");
        }
        System.out.println("Issuer UID from certificate: " + issuerUID);

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

    private boolean isNewCertificateDateValid(Date startDate, Date endDate, BigInteger issuingCertificateSerialNumber) {
        X509Certificate issuingCertificate = (X509Certificate) _certificateRepository.GetCertificateBySerialNumber(keyStorePassword, issuingCertificateSerialNumber);
        Date issuingStartDate = issuingCertificate.getNotBefore();
        Date issuingEndDate = issuingCertificate.getNotAfter();
        return (startDate.after(issuingStartDate) && endDate.before(issuingEndDate));
    }

    private Account buildSelfSignedIssuer(EntityInfo subjectInfo, Certificate newCertificate) {
        KeyPair kp = Keys.generateKeyPair();
        newCertificate.setSubjectPublicKey(kp.getPublic());
        newCertificate.setSubjectPrivateKey(kp.getPrivate());
        newCertificate.setIssuerPublicKey(newCertificate.getSubjectPublicKey());
        newCertificate.setIssuerPrivateKey(newCertificate.getSubjectPrivateKey());

        Account newIssuer = createNewCertificateEntity(subjectInfo, newCertificate);
        newCertificate.setIssuerInfo(newCertificate.getSubjectInfo());

        return newIssuer;
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
            X500Name subjectName = new JcaX509CertificateHolder((X509Certificate) certificate).getSubject();
            System.out.println("X500Name: " + subjectName);
            newCertificate.setSubjectInfo(subjectName);

            return account;
        }
        System.out.println("Account is new");
        return createNewCertificateEntity(info, newCertificate);
    }

    private Account createNewCertificateEntity(EntityInfo info, Certificate newCertificate) {
        UUID accountId = UUID.randomUUID();
        X500NameBuilder builder = setupBasicCertificateInfo(info, accountId);
        newCertificate.setSubjectInfo(builder.build());

        return new Account(accountId, info.getEmail(), info.getPassword(), new HashSet<>());
    }

    private Account buildIssuer(UUID issuerId, BigInteger serialNumber, Certificate newCertificate)
            throws BadRequestException, CertificateEncodingException {
        String issuerCertificateAlias = _certificateRepository.GetCertificateAliasBySerialNumber (keyStorePassword, serialNumber);

        if (issuerCertificateAlias == null) {
            throw new BadRequestException("Issuer with given serial number not found");
        }

        Optional<KeystoreRowInfo> issuerCertificatesInfo =  _keystoreRowInfoRepository.findByAlias(issuerCertificateAlias);
        KeystoreRowInfo rowInfo = null;
        if(issuerCertificatesInfo.isPresent()){
            rowInfo = issuerCertificatesInfo.get();
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

        //X500NameBuilder builder = setupBasicCertificateInfo(info);
        Account account = _accountService.findById(issuerId);
        //builder.addRDN(BCStyle.UID, account.getId().toString());

        java.security.cert.Certificate certificate = _certificateRepository.GetCertificate(issuerCertificateAlias, keyStorePassword);
        X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) certificate).getSubject();
        System.out.println("X500Name: " + issuerName);

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
