package pkibackend.pkibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UpdatePasswordDto {
    private String oldPassword;
    private String newPassword;
}
