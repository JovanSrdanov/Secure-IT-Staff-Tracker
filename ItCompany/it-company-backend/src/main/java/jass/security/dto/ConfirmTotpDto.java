package jass.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmTotpDto {
    private String code;
    private String twoPhaseAuthConfirmationToken;
}
