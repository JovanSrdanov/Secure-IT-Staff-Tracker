package jass.security.dto;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordLoginResponseDto {
    private String twoPhaseAuthConfirmationToken;
}
