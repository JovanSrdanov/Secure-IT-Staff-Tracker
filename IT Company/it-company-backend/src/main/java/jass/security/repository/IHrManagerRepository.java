package jass.security.repository;

import jass.security.model.HrManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IHrManagerRepository extends JpaRepository<HrManager, UUID> {
}
