package pkibackend.pkibackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Transient;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Certificate {
    Account account;

    private BigInteger serialNumber;
    private Date startDate;
    private Date endDate;

    @JsonIgnore
    @Transient
    private X509Certificate x509Certificate;
}
