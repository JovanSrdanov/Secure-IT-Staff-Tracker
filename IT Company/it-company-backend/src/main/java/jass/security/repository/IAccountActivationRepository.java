package jass.security.repository;

import jass.security.model.AccountActivation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IAccountActivationRepository extends JpaRepository<AccountActivation, UUID>  {
    public void deleteAccountActivationById(UUID id);
}
