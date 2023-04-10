package pkibackend.pkibackend.Utilities;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;

import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class CertificateUtilities {
    //Objasnjenje: https://stackoverflow.com/questions/16197253/retrieve-full-extension-value-from-certificate
    public static BigInteger GetIssuerSerialNumber(X509Certificate certificate){
        String oid = Extension.authorityKeyIdentifier.getId();
        byte[] extensionValue =  certificate.getExtensionValue(oid);

        ASN1OctetString akiOc = ASN1OctetString.getInstance(extensionValue);
        AuthorityKeyIdentifier aki = AuthorityKeyIdentifier.getInstance(akiOc.getOctets());

        byte[] serialNumberB = aki.getKeyIdentifier();
        return new BigInteger(serialNumberB);
    }

    // Source: https://stackoverflow.com/questions/12092457/how-to-check-if-x509certificate-is-ca-certificate
    // 2nd answer
    public static boolean CheckCA(X509Certificate certificate){
       return  certificate.getBasicConstraints() != -1;
    }
}
