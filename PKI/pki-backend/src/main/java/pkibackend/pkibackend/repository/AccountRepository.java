package pkibackend.pkibackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pkibackend.pkibackend.model.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByEmail(String email);
    List<Account> findAllByIdIsNot(UUID accountId);
}
