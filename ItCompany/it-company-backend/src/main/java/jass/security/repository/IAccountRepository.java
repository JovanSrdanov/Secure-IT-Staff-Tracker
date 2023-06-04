package jass.security.repository;

import jass.security.model.Account;
import jass.security.model.RegistrationRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAccountRepository extends JpaRepository<Account, UUID> {
    Account findByEmail(String email);
    @Query("SELECT u FROM Account u WHERE u.status = ?1")
    ArrayList<Account> findAllByStatus(RegistrationRequestStatus status);
    List<Account> findByRolesName(String roleName);
}
