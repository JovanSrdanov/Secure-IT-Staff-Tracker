package pkibackend.pkibackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.cert.X509Certificate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Certificate {
    Account account;

    private String serialNumber;
    private Date startDate;
    private Date endDate;

    private Boolean isRevoked;

    private X509Certificate x509Certificate;
}
