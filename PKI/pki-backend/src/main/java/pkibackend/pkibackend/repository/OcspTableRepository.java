package pkibackend.pkibackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pkibackend.pkibackend.model.OcspTable;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

public interface OcspTableRepository extends JpaRepository<OcspTable, UUID> {
    Optional<OcspTable> findByCaSerialNumber(BigInteger serialNumber);
}
