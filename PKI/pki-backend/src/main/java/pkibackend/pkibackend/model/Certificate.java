package pkibackend.pkibackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import pkibackend.pkibackend.Utilities.CertificateUtilities;

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
    private BigInteger issuerSerialNumber;

    @JsonIgnore
    @Transient
    private boolean isCa;

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

    //Initializes only things available from certificate
    public Certificate(java.security.cert.Certificate rawCertificate){
        this.x509Certificate = (X509Certificate) rawCertificate;

        this.serialNumber = this.x509Certificate.getSerialNumber();
        this.startDate = this.x509Certificate.getNotBefore();
        this.endDate = this.x509Certificate.getNotAfter();
        this.subjectPublicKey = this.x509Certificate.getPublicKey();
        // Ovo baca neki exception (totalno srbija)
        // this.subjectInfo = X500Name.getInstance(this.x509Certificate.getSubjectX500Principal());
        this.issuerSerialNumber = CertificateUtilities.GetIssuerSerialNumber(this.x509Certificate);
        this.isCa = CertificateUtilities.CheckCA(this.x509Certificate);
    }
}
