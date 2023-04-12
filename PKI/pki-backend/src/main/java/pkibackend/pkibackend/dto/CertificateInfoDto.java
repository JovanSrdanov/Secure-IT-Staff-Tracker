package pkibackend.pkibackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;

import java.math.BigInteger;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class CertificateInfoDto {
    private CertificateEntityInfoDto subjectInfo;
    private CertificateEntityInfoDto issuerInfo;
    private Date startDate;
    private Date endDate;
    private String alias;
    private boolean isRevoked;
    private BigInteger issuerSerialNumber;
    private BigInteger serialNumber;
    private boolean isCa;
}
