package pkibackend.pkibackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import pkibackend.pkibackend.certificates.CertificateGenerator;
import pkibackend.pkibackend.dto.BooleanResponse;
import pkibackend.pkibackend.dto.CertificateSerialNum;
import pkibackend.pkibackend.dto.CreateCertificateInfo;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.service.interfaces.ICertificateService;

import java.math.BigInteger;
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
        // TODO Jovan: iz jwt-a da se izvuku ifno o issuer-u
        // TODO Strahinja: provera dal moze da izda sertifikat nekom drugom (dal je CA il nije,
        // da li je zaista issuer-ov sertifikat

        try {
            Certificate createdCertificate = _certificateService.generateCertificate(info);
            return new ResponseEntity<>(createdCertificate, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error while creating certificate", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (BadRequestException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("revoke")
    public ResponseEntity revokeCertificate(@RequestBody CertificateSerialNum certificateSerialNum){

        try{
            _certificateService.revoke(certificateSerialNum.getSerialNumber());
            return new ResponseEntity<>("Certificate revoked", HttpStatus.NO_CONTENT);
        }
        catch (NotFoundException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //Cisto napravljeno za testiranje (ko zna mozda i zatreba)
    @GetMapping("revoke/{serialNumber}")
    public ResponseEntity<BooleanResponse> checkIfRevoked(@PathVariable("serialNumber") BigInteger certificateSerialNum){
        boolean revoked = _certificateService.isRevoked(certificateSerialNum);
        return new ResponseEntity<BooleanResponse>(new BooleanResponse(revoked), HttpStatus.OK);
    }
}
