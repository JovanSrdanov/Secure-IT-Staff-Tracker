package jass.security.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PasswordLoginResponse {
    @Id
    private UUID id;
    private String email;
    private String twoPhaseAuthConfirmationToken;
}
