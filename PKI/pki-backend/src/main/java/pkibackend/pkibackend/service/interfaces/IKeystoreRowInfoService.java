package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.model.KeystoreRowInfo;

import java.math.BigInteger;
import java.util.Optional;

public interface IKeystoreRowInfoService extends ICrudService<Certificate>{
    Optional<KeystoreRowInfo> findByCertificateSerialNumber(BigInteger serialNumber);
}
