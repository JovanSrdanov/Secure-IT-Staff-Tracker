package jass.security.repository;

import jass.security.model.PasswordlessLoginToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IPasswordlessLoginTokenRepository extends JpaRepository<PasswordlessLoginToken, UUID> {
    Optional<PasswordlessLoginToken> findPasswordlessLoginTokenByToken(String token);
}
