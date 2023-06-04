package jass.security.repository;

import jass.security.model.SoftwareEngineer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ISoftwareEngineerRepository extends JpaRepository<SoftwareEngineer, UUID> {
}
