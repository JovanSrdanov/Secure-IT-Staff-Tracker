package pkibackend.pkibackend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pkibackend.pkibackend.keystore.KeyStoreReader;
import pkibackend.pkibackend.keystore.KeyStoreWriter;
import pkibackend.pkibackend.model.Certificate;

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
        PrivateKey pk = certificate.getAccount().getPrivateKey();
        _writer.write(alias, pk, keyPassword.toCharArray(), certificate.getX509Certificate());
        _writer.saveKeyStore("src/main/resources/static/example.jks",  storePassword.toCharArray());
    }

    public java.security.cert.Certificate GetCertificate(String alias, String storePassword) {
         return _reader.readCertificate
                ("src/main/resources/static/example.jks", storePassword, alias);

    }
}
