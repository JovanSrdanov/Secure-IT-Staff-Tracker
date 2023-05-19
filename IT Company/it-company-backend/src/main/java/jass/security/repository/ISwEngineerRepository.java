package jass.security.repository;

import jass.security.model.SoftwareEngineer;
import jass.security.model.SwEngineerProjectStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ISwEngineerRepository extends JpaRepository<SoftwareEngineer, UUID> {
}
