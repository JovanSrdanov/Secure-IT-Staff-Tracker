package jass.security.repository;

import jass.security.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IAdministratorRepository extends JpaRepository<Administrator, UUID> {
}
