package pkibackend.pkibackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;

import javax.persistence.Transient;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Certificate {
    //sva polja sem X509Certificate su pomocna

    private BigInteger serialNumber;
    private Date startDate;
    private Date endDate;

    @JsonIgnore
    @Transient
    private PrivateKey subjectPrivateKey;
    @JsonIgnore
    @Transient
    private PrivateKey issuerPrivateKey;

    @JsonIgnore
    @Transient
    private PublicKey subjectPublicKey;
    @JsonIgnore
    @Transient
    private PublicKey issuerPublicKey;

    @Transient
    private X500Name subjectInfo;
    @Transient
    private X500Name issuerInfo;

    @JsonIgnore
    @Transient
    private X509Certificate x509Certificate;
}
