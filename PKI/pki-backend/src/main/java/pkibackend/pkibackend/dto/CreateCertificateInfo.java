package pkibackend.pkibackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class CreateCertificateInfo {
    private EntityInfo subjectInfo;
    //TODO dovoljno je samo poslati issuerid
    private EntityInfo issuerInfo;
    private Date startDate;
    private Date endDate;

    // serijski broj sertifikata koji je koriscen za potpisivanje
    // TODO Stefan: staviti da je critical
    private BigInteger issuingCertificateSerialNumber;

    // pogledaj addExtensions metodu u CertificateGenerator-u
    private Map<String, String> extensions;

    // alias novog sertifikata
    String alias;
}