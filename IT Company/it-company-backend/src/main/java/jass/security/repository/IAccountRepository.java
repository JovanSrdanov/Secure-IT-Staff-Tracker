package jass.security.repository;

import jass.security.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IAccountRepository extends JpaRepository<Account, UUID> {
    Account findByEmail(String email);
}
