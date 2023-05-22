package pkibackend.pkibackend.keystore;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.stereotype.Component;
import pkibackend.pkibackend.model.Account;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Component
public class KeyStoreReader {
    private final KeyStore keyStore;

    public KeyStoreReader() {
        try {
            keyStore = KeyStore.getInstance("PKCS12", "SUN");
        } catch (KeyStoreException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public Account readAccountFromStore(String keyStoreFile, String alias, char[] password, char[] keyPass) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            keyStore.load(in, password);

            Certificate cert = keyStore.getCertificate(alias);

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPass);

            X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            //return new Account(privateKey, cert.getPublicKey(), issuerName);
            return new Account();

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException
                 | UnrecoverableKeyException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Certificate readCertificate(String keyStoreFile, String keyStorePass, String alias) {
        try {
            //KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            keyStore.load(in, keyStorePass.toCharArray());

            if (keyStore.isKeyEntry(alias)) {
                return keyStore.getCertificate(alias);
            }

            return null;

        } catch (KeyStoreException | NoSuchAlgorithmException
                 | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PrivateKey readPrivateKey(String keyStoreFile, String keyStorePass, String alias, String pass) {
        try {
            //KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            keyStore.load(in, keyStorePass.toCharArray());

            if (keyStore.isKeyEntry(alias)) {
                return (PrivateKey) keyStore.getKey(alias, pass.toCharArray());
            }

            return null;

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException |
                 IOException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public String readCertificateAliasBySerialNumber(String keyStoreFile, String keyStorePass, BigInteger serialNumber) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            keyStore.load(in, keyStorePass.toCharArray());

            Enumeration<String> aliasEnum = keyStore.aliases();

            while (aliasEnum.hasMoreElements()) {
                String alias = aliasEnum.nextElement();
                Certificate cert = keyStore.getCertificate(alias);
                // ako je sertifikat odgovarajuceg tipa i poklapaju se serijski brojevi
                if (cert instanceof X509Certificate x509Cert && x509Cert.getSerialNumber().toString().equals(serialNumber.toString())) {
                    // Certificate with the specified serial number found
                    //Debugger: "am I joke to you :O"
//                    System.out.println("Alias: " + alias);
//                    System.out.println("Certificate: " + x509Cert);
                    // You can use x509Cert for further operations as needed
                    return alias;
                }
            }
        } catch (NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public Iterable<Certificate> GetAllCertificates(String keyStoreFile, String keyStorePass) {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            keyStore.load(in, keyStorePass.toCharArray());

            List<Certificate> certificates = new ArrayList<>();

            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                certificates.add(keyStore.getCertificate(alias));
            }

            return certificates;
        } catch (CertificateException | IOException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<pkibackend.pkibackend.model.Certificate> GetChildren(String keyStoreFile, String keyStorePass,
                                                                         BigInteger issuerSerialNumber) {
        Iterable<java.security.cert.Certificate> certificates = GetAllCertificates(keyStoreFile, keyStorePass);

        List<pkibackend.pkibackend.model.Certificate> children = new ArrayList<pkibackend.pkibackend.model.Certificate>();

        for (Certificate certificate : certificates) {
            pkibackend.pkibackend.model.Certificate x509Cert = new pkibackend.pkibackend.model.Certificate(certificate);
            if (x509Cert.getIssuerSerialNumber().equals(issuerSerialNumber) &&
                    !x509Cert.getIssuerSerialNumber().equals(x509Cert.getSerialNumber()) )
            {
                children.add(x509Cert);
            }
        }

        return children;
    }

    public Boolean findAliasInKeystore(String keyStoreFile, String alias, String keyStorePassword) {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            keyStore.load(in, keyStorePassword.toCharArray());

            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                if (alias.equals(aliases.nextElement())) {
                    System.out.println("Found alias: " + alias);
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            // kad je self-signed sertifikat keystore ne postoji jos pa nije nasao alias
            return false;
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}