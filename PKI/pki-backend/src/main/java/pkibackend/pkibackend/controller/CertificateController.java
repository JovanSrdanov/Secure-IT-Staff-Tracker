package pkibackend.pkibackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pkibackend.pkibackend.dto.CreateCertificateInfo;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.service.interfaces.ICertificateService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("certificate")
public class CertificateController {
    private final ICertificateService _certificateService;

    public CertificateController(ICertificateService certificateService) {
        _certificateService = certificateService;
    }

    @PostMapping("")
    public ResponseEntity<String> createCertificate(@RequestBody CreateCertificateInfo info) {
        Account issuer = new Account();
        Account subject = new Account();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date startDate = format.parse("2023-03-25");
            Date endDate = format.parse("2028-03-25");

            //new Certificate(issuer, subject, startDate, endDate, "1")
            _certificateService.generateCertificate(new Certificate());
            return new ResponseEntity<>("Created", HttpStatus.CREATED);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
