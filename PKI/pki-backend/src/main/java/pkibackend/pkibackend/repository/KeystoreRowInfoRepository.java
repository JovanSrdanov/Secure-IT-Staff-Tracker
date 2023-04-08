package pkibackend.pkibackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pkibackend.pkibackend.model.KeystoreRowInfo;

import java.util.Optional;
import java.util.UUID;

public interface KeystoreRowInfoRepository extends JpaRepository<KeystoreRowInfo, UUID> {
    Optional<KeystoreRowInfo> findByAlias(String alias);
}
