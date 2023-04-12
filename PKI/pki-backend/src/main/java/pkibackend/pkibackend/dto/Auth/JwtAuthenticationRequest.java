package pkibackend.pkibackend.dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class JwtAuthenticationRequest {

    private String email;
    private String password;

   
}
