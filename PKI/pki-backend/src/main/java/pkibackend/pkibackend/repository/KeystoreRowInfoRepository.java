package pkibackend.pkibackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pkibackend.pkibackend.model.KeystoreRowInfo;

import java.util.List;
import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

public interface KeystoreRowInfoRepository extends JpaRepository<KeystoreRowInfo, UUID> {
    Optional<KeystoreRowInfo> findByAlias(String alias);
    List<KeystoreRowInfo> findAll();
    Optional<KeystoreRowInfo> findByCertificateSerialNumber(BigInteger serialNumber);
}
