package pkibackend.pkibackend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pkibackend.pkibackend.keystore.KeyStoreReader;
import pkibackend.pkibackend.keystore.KeyStoreWriter;
import pkibackend.pkibackend.model.Certificate;

import java.math.BigInteger;
import java.security.PrivateKey;

@Component
public class CertificateRepository {
    private final KeyStoreReader _reader;
    private final KeyStoreWriter _writer;
    @Value("${keystoresPath}")
    private String keystoresPath;

    @Autowired
    public CertificateRepository(KeyStoreReader reader, KeyStoreWriter writer) {
        _reader = reader;
        _writer = writer;
    }

    public void SaveCertificate(String keyStoreName, String keyStorePassword, Certificate certificate, String alias, String keyPassword) {
        _writer.loadKeyStore(this.keystoresPath + keyStoreName,  keyStorePassword.toCharArray());
        PrivateKey pk = certificate.getSubjectPrivateKey();
        _writer.write(alias, pk, keyPassword.toCharArray(), certificate.getX509Certificate());
        _writer.saveKeyStore(this.keystoresPath + keyStoreName,  keyStorePassword.toCharArray());
    }

    public java.security.cert.Certificate GetCertificate(String alias, String keyStoreName,  String storePassword) {
         return _reader.readCertificate
                (this.keystoresPath + keyStoreName, storePassword, alias);
    }

    public PrivateKey GetCertificatePrivateKey(String keyStoreName,
            String keyStorePass, String alias, String pass) {
        return _reader.readPrivateKey(this.keystoresPath + keyStoreName, keyStorePass,
                alias, pass);
    }

    public String GetCertificateAliasBySerialNumber(String keyStoreName, String keyStorePass, BigInteger serialNumber) {
        return _reader.readCertificateAliasBySerialNumber(this.keystoresPath + keyStoreName,
                keyStorePass, serialNumber);
    }

    public java.security.cert.Certificate GetCertificateBySerialNumber(String keyStoreName, String  keyStorePass,BigInteger serialNumber){
        String alias =  _reader.readCertificateAliasBySerialNumber(this.keystoresPath + keyStoreName,
                keyStorePass, serialNumber);
        if (alias == null) {
            return null;
        }
        return  GetCertificate(alias, keyStoreName, keyStorePass);
    }

    public Iterable<Certificate> GetChildren(String keyStoreName, String keyStorePass, BigInteger issuerSerialNumber) {
        return _reader.GetChildren(keystoresPath + keyStoreName, keyStorePass, issuerSerialNumber);
    }

    public Boolean aliasPresentInKeystore(String keyStoreName, String alias, String keyStorePass) {
        return _reader.findAliasInKeystore(this.keystoresPath + keyStoreName, alias, keyStorePass);
    }
}
