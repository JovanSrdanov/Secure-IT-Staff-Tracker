package pkibackend.pkibackend.controller;

import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pkibackend.pkibackend.Utilities.UniqueFIleCreator;
import pkibackend.pkibackend.dto.BooleanResponse;
import pkibackend.pkibackend.dto.CertificateInfoDto;
import pkibackend.pkibackend.dto.CertificateSerialNum;
import pkibackend.pkibackend.dto.CreateCertificateInfo;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.exceptions.InternalServerErrorException;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.model.KeystoreRowInfo;
import pkibackend.pkibackend.service.interfaces.IAccountService;
import pkibackend.pkibackend.service.interfaces.ICertificateService;
import pkibackend.pkibackend.service.interfaces.IKeystoreRowInfoService;

import java.io.*;
import java.math.BigInteger;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Optional;

@RestController
@RequestMapping("certificate")
public class CertificateController {
    private final ICertificateService _certificateService;
    private final IAccountService _accountService;
    private final IKeystoreRowInfoService _keystoreRowInfoService;

    public CertificateController(ICertificateService certificateService, IAccountService accountService,
                                 IKeystoreRowInfoService keystoreRowInfoService) {
        _certificateService = certificateService;
        _accountService = accountService;
        _keystoreRowInfoService = keystoreRowInfoService;
    }

    @PreAuthorize("hasAnyRole('ROLE_PKI_ADMIN','ROLE_CERTIFICATE_USER')")
    @PostMapping()
    public ResponseEntity<?> createCertificate(@RequestBody CreateCertificateInfo info, Principal principal) {

        try {
            Account a = null;
            a = _accountService.findAccountByEmail(principal.getName());

            if (!_accountService.isAccountAdmin(a) && !_certificateService.isInKeystore(a.getKeyStoreRowsInfo(), info.getIssuingCertificateSerialNumber())) {
                return new ResponseEntity<>("You do not own issuing certificate!", HttpStatus.FORBIDDEN);
            }


            if (info.getIssuingCertificateSerialNumber() != null) {
                try {
                    if (!_certificateService.isChainValid(info.getIssuingCertificateSerialNumber())) {
                        return new ResponseEntity<>("Issuing certificate is not valid!", HttpStatus.CONFLICT);
                    }
                } catch (BadRequestException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
            }


            Certificate createdCertificate = _certificateService.generateCertificate(info);
            return new ResponseEntity<>(createdCertificate.getSerialNumber(), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error while creating certificate", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (CertificateEncodingException | InternalServerErrorException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_PKI_ADMIN')")
    @PostMapping("revoke")
    public ResponseEntity revokeCertificate(@RequestBody CertificateSerialNum certificateSerialNum) {
        try {
            _certificateService.revoke(certificateSerialNum.getSerialNumber());
            return new ResponseEntity<>("Certificate revoked", HttpStatus.NO_CONTENT);
        } catch (BadRequestException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //Cisto napravljeno za testiranje (ko zna mozda i zatreba)

    public ResponseEntity checkIfRevoked(@PathVariable("serialNumber") BigInteger certificateSerialNum) {
        boolean revoked = false;
        try {
            revoked = _certificateService.isRevoked(certificateSerialNum);
        } catch (BadRequestException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<BooleanResponse>(new BooleanResponse(revoked), HttpStatus.OK);
    }

    @GetMapping("valid/{serialNumber}")
    public ResponseEntity checkIfValid(@PathVariable("serialNumber") BigInteger certificateSerialNum) {
        boolean isValid = false;
        try {
            isValid = _certificateService.isChainValid(certificateSerialNum);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new BooleanResponse(isValid), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_PKI_ADMIN','ROLE_CERTIFICATE_USER')")
    @GetMapping(value = "download/{serialNumber}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity downloadCertificate(@PathVariable("serialNumber") BigInteger certificateSerialNum) throws IOException {
        // Todo Strahina moze da skida samo svoje ako nije admin
        X509Certificate x509Certificate = null;
        try {
            x509Certificate = _certificateService.GetCertificateBySerialNumber(certificateSerialNum);
        } catch (BadRequestException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        try {
            byte[] certificateBytes = x509Certificate.getEncoded();
            // Convert the X509Certificate to PEM format
            ByteArrayOutputStream pemStream = new ByteArrayOutputStream();
            JcaPEMWriter certPemWriter = new JcaPEMWriter(new OutputStreamWriter(pemStream));
            certPemWriter.writeObject(x509Certificate);
            certPemWriter.close();

            byte[] pemBytes = pemStream.toByteArray();


            // skidanje alias-a, privatnog kljuca, lozinke od keystore-a od sertifikata
            Optional<KeystoreRowInfo> downloadedCertificateKeystoreInfo = _keystoreRowInfoService.findByCertificateSerialNumber(certificateSerialNum);
            if (downloadedCertificateKeystoreInfo.isEmpty()) {
                return new ResponseEntity("Certificate not found in keystore", HttpStatus.NOT_FOUND);
            }
            String keystoreName = downloadedCertificateKeystoreInfo.get().getKeystoreName();
            String keyStorePassword = downloadedCertificateKeystoreInfo.get().getPassword();
            String downloadedCertificateAlias = downloadedCertificateKeystoreInfo.get().getAlias();
            String downloadedCertificateRowPassword = downloadedCertificateKeystoreInfo.get().getRowPassword();
            PrivateKey downloadedCertificatePrivateKey = _certificateService.GetCertificatePrivateKey(keystoreName,
                    keyStorePassword, downloadedCertificateAlias, downloadedCertificateRowPassword);
            String downloadedCertificatePrivateKeyPassword = downloadedCertificateKeystoreInfo.get().getRowPassword();

            // kreiranje tekstualnih info za sertifikat za https
            File file = UniqueFIleCreator.createUniqueFile("certInfo.txt");
            FileWriter writer = new FileWriter(file);
            writer.write("keystore password: " + keyStorePassword + ", alias: " + downloadedCertificateAlias +
                    ", key password: " + downloadedCertificatePrivateKeyPassword);
            writer.close();

            // cuvanje privatnog kljuca koji odgovara sertifikatu
            File keyFile = UniqueFIleCreator.createUniqueFile("privateKey.key");

            FileOutputStream fileOutputStream = new FileOutputStream(keyFile);
            PEMWriter pemWriter = new PEMWriter(new OutputStreamWriter(fileOutputStream));

            pemWriter.writeObject(downloadedCertificatePrivateKey);

            pemWriter.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "certificate.crt");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pemBytes);
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ROLE_PKI_ADMIN')")
    @GetMapping("all")
    public ResponseEntity findAllAdmin() {

        Iterable<CertificateInfoDto> result = null;
        try {
            result = _certificateService.findAllAdmin();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_PKI_ADMIN')")
    @GetMapping("allCa")
    public ResponseEntity findAllCaAdmin() {
        try {
            Iterable<CertificateInfoDto> result = _certificateService.findAllCaAdmin();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PreAuthorize("hasRole('ROLE_CERTIFICATE_USER')")
    @GetMapping("loggedIn")
    public ResponseEntity findAllForLoggedIn(Principal principal) {
        try {
            Account a = _accountService.findAccountByEmail(principal.getName());
            Iterable<CertificateInfoDto> result = _certificateService.findAllForLoggedIn(a.getId());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CERTIFICATE_USER')")
    @GetMapping("loggedIn/validCa")
    public ResponseEntity findAllInvalidForLoggedIn(Principal principal) {
        try {
            Account a = _accountService.findAccountByEmail(principal.getName());
            Iterable<CertificateInfoDto> result = _certificateService.findAllValidCaForLoggedIn(a.getId());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
