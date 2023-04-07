package pkibackend.pkibackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pkibackend.pkibackend.certificates.CertificateGenerator;
import pkibackend.pkibackend.dto.CreateCertificateInfo;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.service.interfaces.ICertificateService;

import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("certificate")
public class CertificateController {
    private final ICertificateService _certificateService;

    public CertificateController(ICertificateService certificateService) {
        _certificateService = certificateService;
    }

    @PostMapping()
    public ResponseEntity<?> createCertificate(@RequestBody CreateCertificateInfo info) {
        try {
            Certificate createdCertificate = _certificateService.generateCertificate(info);
            return new ResponseEntity<>(createdCertificate, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error while creating certificate", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
