package pkibackend.pkibackend.certificates;

import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Component;
import pkibackend.pkibackend.model.Account;

import java.math.BigInteger;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;

@Component
public class CertificateGenerator {
    public CertificateGenerator() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static X509Certificate generateCertificate(Account subject, Account issuer,
                                                      Date startDate, Date endDate,
                                                      BigInteger serialNumber, Map<String, String> extensions,
                                                      BigInteger issuingCertificateSerialNumber) {
        try {
            //Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc pravi se builder za objekat
            //Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za potpisivanje sertifikata
            //Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje sertifiakta
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            //Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
            builder = builder.setProvider("BC");

            //Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(issuer.getPrivateKey());

            //Postavljaju se podaci za generisanje sertifiakta
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                    issuer.getX500Name(),
                    serialNumber,
                    startDate,
                    endDate,
                    subject.getX500Name(),
                    subject.getPublicKey());
            
            addExtensions(certGen, extensions, subject, issuingCertificateSerialNumber);
            
            //Generise se sertifikat
            X509CertificateHolder certHolder = certGen.build(contentSigner);

            //Builder generise sertifikat kao objekat klase X509CertificateHolder
            //Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            //Konvertuje objekat u sertifikat
            return certConverter.getCertificate(certHolder);

        } catch (IllegalArgumentException | IllegalStateException | OperatorCreationException | CertificateException |
                 CertIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void addExtensions(X509v3CertificateBuilder certGen, Map<String, String> extensions,
                                      Account subject, BigInteger issuingCertificateSerialNumber)
            throws CertIOException {
        if (extensions.containsKey("keyUsage")) {
            KeyUsage usage;
            switch (extensions.get("keyUsage")) {
                case "keyEncipherment" -> {
                    usage = new KeyUsage(KeyUsage.keyEncipherment);
                    certGen.addExtension(Extension.keyUsage, false, usage);
                }
                case "dataEncipherment" -> {
                    usage = new KeyUsage(KeyUsage.dataEncipherment);
                    certGen.addExtension(Extension.keyUsage, false, usage);
                }
                default -> {
                    usage = new KeyUsage(KeyUsage.digitalSignature);
                    certGen.addExtension(Extension.keyUsage, false, usage);
                }
            }
        }
        if (extensions.containsKey("subjectKeyIdentifier")) {
            SubjectKeyIdentifier identifier = new SubjectKeyIdentifier(subject.getPublicKey().getEncoded());
            certGen.addExtension(Extension.subjectKeyIdentifier, false, identifier);
        }
        // odredjuje dal je CA il nije
        if (extensions.containsKey("basicConstraints")) {
            boolean isCA = extensions.get("basicConstraints").equals("CA");
            certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(isCA));
        }
        // opciono cuva identifikator za javni kljuc issuer-a i/ili serijski broj sertifikata koji je
        // iskoriscen za potpis
        if (extensions.containsKey("authorityKeyIdentifier") && issuingCertificateSerialNumber != null) {
            AuthorityKeyIdentifier identifier = new AuthorityKeyIdentifier(
                    issuingCertificateSerialNumber.toByteArray());
            certGen.addExtension(Extension.authorityKeyIdentifier, false, identifier);
        }
    }
}
