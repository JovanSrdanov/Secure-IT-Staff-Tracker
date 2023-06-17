package jass.security.repository;

import jass.security.model.RejectedMail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IRejectedMailRespository extends JpaRepository<RejectedMail, UUID> {
    RejectedMail findByEmail(String email);
}
