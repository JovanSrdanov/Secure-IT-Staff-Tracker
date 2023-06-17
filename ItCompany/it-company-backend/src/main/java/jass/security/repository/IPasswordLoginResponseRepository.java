package jass.security.repository;

import jass.security.model.PasswordLoginResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IPasswordLoginResponseRepository extends JpaRepository<PasswordLoginResponse, UUID> {
    Optional<PasswordLoginResponse> findByTwoPhaseAuthConfirmationToken(String token);
}
