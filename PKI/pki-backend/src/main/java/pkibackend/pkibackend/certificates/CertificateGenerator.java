package pkibackend.pkibackend.certificates;

import org.bouncycastle.asn1.*;
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
import pkibackend.pkibackend.model.Certificate;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Component
public class CertificateGenerator {
    public CertificateGenerator() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static X509Certificate generateCertificate(Certificate newCertificate,
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
            ContentSigner contentSigner = builder.build(newCertificate.getIssuerPrivateKey());

            //Postavljaju se podaci za generisanje sertifiakta
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                    newCertificate.getIssuerInfo(),
                    serialNumber,
                    startDate,
                    endDate,
                    newCertificate.getSubjectInfo(),
                    newCertificate.getSubjectPublicKey());

            addExtensions(certGen, extensions, newCertificate, issuingCertificateSerialNumber);

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
                                      Certificate newCertificate, BigInteger issuingCertificateSerialNumber)
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
                // ovo znaci da kljuc moze da se koristi u "key agreement protokolima",
                // 2 strane se dogovore da koriste deljeni tajni kljuc. Koristi se u
                // SSL/TLS enkripcijama kod web aplikacija
                case "keyAgreement" -> {
                    usage = new KeyUsage(KeyUsage.keyAgreement);
                    certGen.addExtension(Extension.keyUsage, false, usage);
                }
                case "certificateSigning" -> {
                    usage = new KeyUsage(KeyUsage.keyCertSign);
                    certGen.addExtension(Extension.keyUsage, false, usage);
                }
                default -> {
                    usage = new KeyUsage(KeyUsage.digitalSignature);
                    certGen.addExtension(Extension.keyUsage, false, usage);
                }
            }
        }
        if (extensions.containsKey("subjectKeyIdentifier")) {
            SubjectKeyIdentifier identifier = new SubjectKeyIdentifier(newCertificate.getSubjectPublicKey().getEncoded());
            certGen.addExtension(Extension.subjectKeyIdentifier, false, identifier);
        }
        // odredjuje dal je CA il nije
        if (extensions.containsKey("basicConstraints")) {
            boolean isCA = extensions.get("basicConstraints").equals("CA");
            certGen.addExtension(Extension.basicConstraints, false, new BasicConstraints(isCA));
        }
//        else {
//            certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
//        }

        // opciono cuva identifikator za javni kljuc issuer-a i/ili serijski broj sertifikata koji je
        // iskoriscen za potpis
//        SubjectPublicKeyInfo issuerPublicKeyInfo = SubjectPublicKeyInfo.getInstance(newCertificate.getIssuerPublicKey().getEncoded());
//        try {
//            DEROctetString publicKeyOctetString = new DEROctetString(issuerPublicKeyInfo.getEncoded());
//            GeneralName generalName = new GeneralName(GeneralName.otherName, publicKeyOctetString);
//            GeneralName[] generalNames = { generalName };
//            GeneralNames generalNamesExtension = new GeneralNames(generalNames);
//
//            AuthorityKeyIdentifier identifier = new AuthorityKeyIdentifier(
//                    generalNamesExtension,
//                    issuingCertificateSerialNumber
//            );
//
//            certGen.addExtension(Extension.authorityKeyIdentifier, false, identifier);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        AuthorityKeyIdentifier identifier = new AuthorityKeyIdentifier(
                //issuingCertificateSerialNumber.toByteArray()
                newCertificate.getIssuerPublicKey().getEncoded()
        );

        certGen.addExtension(Extension.authorityKeyIdentifier, false, identifier);

        // SAN ekstenzija da bi chrome mogao da prepozna sertifikat, zbog HTTPS-a
        GeneralName[] generalName = new GeneralName[3];
        generalName[0] = new GeneralName(GeneralName.dNSName, "localhost");
        generalName[1] = new GeneralName(GeneralName.dNSName, "https://localhost");
        generalName[2] = new GeneralName(GeneralName.iPAddress, "127.0.0.1");

        GeneralNames subjectAltNames = new GeneralNames(generalName);
        //GeneralNames subjectAltNames = new GeneralNames(new GeneralName(GeneralName.dNSName, "localhost"));

        certGen.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);
    }
}
