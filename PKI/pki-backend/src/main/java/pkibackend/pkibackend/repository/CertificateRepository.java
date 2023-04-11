package pkibackend.pkibackend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pkibackend.pkibackend.keystore.KeyStoreReader;
import pkibackend.pkibackend.keystore.KeyStoreWriter;
import pkibackend.pkibackend.model.Certificate;

import java.math.BigInteger;
import java.security.PrivateKey;

// TODO Stefan: nisam siguran dal treba component il nesto drugo
@Component
public class CertificateRepository {
    private final KeyStoreReader _reader;
    private final KeyStoreWriter _writer;

    @Autowired
    public CertificateRepository(KeyStoreReader reader, KeyStoreWriter writer) {
        _reader = reader;
        _writer = writer;
    }

    public void SaveCertificate(Certificate certificate, String storePassword, String alias, String keyPassword) {
        _writer.loadKeyStore("src/main/resources/static/example.jks",  storePassword.toCharArray());
        PrivateKey pk = certificate.getSubjectPrivateKey();
        _writer.write(alias, pk, keyPassword.toCharArray(), certificate.getX509Certificate());
        _writer.saveKeyStore("src/main/resources/static/example.jks",  storePassword.toCharArray());
    }

    public java.security.cert.Certificate GetCertificate(String alias, String storePassword) {
         return _reader.readCertificate
                ("src/main/resources/static/example.jks", storePassword, alias);

    }

    public PrivateKey GetCertificatePrivateKey(
            String keyStorePass, String alias, String pass) {
        return _reader.readPrivateKey("src/main/resources/static/example.jks", keyStorePass,
                alias, pass);
    }

    public String GetCertificateAliasBySerialNumber(String keyStorePass, BigInteger serialNumber) {
        return _reader.readCertificateAliasBySerialNumber("src/main/resources/static/example.jks",
                keyStorePass, serialNumber);
    }

    public java.security.cert.Certificate GetCertificateBySerialNumber(String keyStorePass, BigInteger serialNumber){
        String alias =  _reader.readCertificateAliasBySerialNumber("src/main/resources/static/example.jks",
                keyStorePass, serialNumber);
        if (alias == null) {
            return null;
        }
        return  GetCertificate(alias, keyStorePass);
    }

    public Iterable<Certificate> GetChildren(String keyStoreFile, String keyStorePass, BigInteger issuerSerialNumber) {
        return _reader.GetChildren(keyStoreFile, keyStorePass, issuerSerialNumber);
    }
}
