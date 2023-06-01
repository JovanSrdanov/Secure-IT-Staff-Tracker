package jass.security.repository;

import jass.security.model.AccountRecovery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IAccountRecoveryRepository extends JpaRepository<AccountRecovery, UUID> {
    AccountRecovery findByToken(String token);
}
