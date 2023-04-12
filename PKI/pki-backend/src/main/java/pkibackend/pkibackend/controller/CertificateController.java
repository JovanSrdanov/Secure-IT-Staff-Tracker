package pkibackend.pkibackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import pkibackend.pkibackend.certificates.CertificateGenerator;
import pkibackend.pkibackend.dto.BooleanResponse;
import pkibackend.pkibackend.dto.CertificateInfoDto;
import pkibackend.pkibackend.dto.CertificateSerialNum;
import pkibackend.pkibackend.dto.CreateCertificateInfo;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.exceptions.InternalServerErrorException;
import pkibackend.pkibackend.model.Account;
import org.springframework.http.HttpHeaders;
import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.service.interfaces.ICertificateService;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        // da li je zaista issuer-ov sertifikat)

        try {
            Certificate createdCertificate = _certificateService.generateCertificate(info);
            return new ResponseEntity<>(createdCertificate.getSerialNumber(), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error while creating certificate", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (BadRequestException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (CertificateEncodingException | InternalServerErrorException e) {
            throw new RuntimeException(e);
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

    @GetMapping("valid/{serialNumber}")
    public ResponseEntity<BooleanResponse> checkIfValid(@PathVariable("serialNumber") BigInteger certificateSerialNum){
        boolean isValid = _certificateService.isChainValid(certificateSerialNum);
        return new ResponseEntity<>(new BooleanResponse(isValid), HttpStatus.OK);
    }

    @GetMapping(value = "download/{serialNumber}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable("serialNumber") BigInteger certificateSerialNum) throws IOException {
        X509Certificate x509Certificate = _certificateService.GetCertificateBySerialNumber(certificateSerialNum);
        try {
            byte[] certificateBytes = x509Certificate.getEncoded();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "certificate.crt");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(certificateBytes);
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("all")
    public ResponseEntity<Iterable<CertificateInfoDto>> findAllAdmin(){
        return new ResponseEntity<>(_certificateService.findAllAdmin(), HttpStatus.OK);
    }

    @GetMapping("allCa")
    public ResponseEntity<Iterable<CertificateInfoDto>> findAllCaAdmin(){
        return new ResponseEntity<>(_certificateService.findAllCaAdmin(), HttpStatus.OK);
    }

    //TODO Strahinja: Ovde treba iz jwt-a a ne iz uri-a
    @GetMapping("loggedIn/{accId}")
    public ResponseEntity<Iterable<CertificateInfoDto>> findAllForLoggedIn(@PathVariable("accId") UUID accId){
        return new ResponseEntity<>(_certificateService.findAllForLoggedIn(accId), HttpStatus.OK);
    }

    @GetMapping("loggedIn/validCa/{accId}")
    public ResponseEntity<Iterable<CertificateInfoDto>> findAllInvalidForLoggedIn(@PathVariable("accId") UUID accId){
        return new ResponseEntity<>(_certificateService.findAllValidCaForLoggedIn(accId), HttpStatus.OK);
    }
}
