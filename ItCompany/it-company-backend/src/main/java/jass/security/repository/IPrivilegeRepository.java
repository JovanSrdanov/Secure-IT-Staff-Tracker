package jass.security.repository;

import jass.security.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IPrivilegeRepository extends JpaRepository<Privilege, UUID> {
    Privilege findByName(String name);
}
