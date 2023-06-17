package pkibackend.pkibackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;

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
    public Certificate(java.security.cert.Certificate rawCertificate) {
        this.x509Certificate = (X509Certificate) rawCertificate;

        this.serialNumber = this.x509Certificate.getSerialNumber();
        this.startDate = this.x509Certificate.getNotBefore();
        this.endDate = this.x509Certificate.getNotAfter();
        this.subjectPublicKey = this.x509Certificate.getPublicKey();
        // Ovo baca neki exception (totalno srbija)
        // this.subjectInfo = X500Name.getInstance(this.x509Certificate.getSubjectX500Principal());
        //this.issuerSerialNumber = GetIssuerSerialNumber(this.x509Certificate);
        this.isCa = CheckCA(this.x509Certificate);
    }

    //Objasnjenje: https://stackoverflow.com/questions/16197253/retrieve-full-extension-value-from-certificate
    public static BigInteger GetIssuerSerialNumber(X509Certificate certificate) {
        String oid = Extension.authorityKeyIdentifier.getId();
        byte[] extensionValue = certificate.getExtensionValue(oid);
        if (extensionValue == null) {
            throw new IllegalArgumentException("Authority Key Identifier extension not found");
        }

        ASN1OctetString akiOc = ASN1OctetString.getInstance(extensionValue);
        AuthorityKeyIdentifier aki = AuthorityKeyIdentifier.getInstance(akiOc.getOctets());

        byte[] serialNumberB = aki.getKeyIdentifier();
        return new BigInteger(serialNumberB);
    }

    // Source: https://stackoverflow.com/questions/12092457/how-to-check-if-x509certificate-is-ca-certificate
    // 2nd answer
    public static boolean CheckCA(X509Certificate certificate) {
        return certificate.getBasicConstraints() != -1;
    }
}
