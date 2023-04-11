package pkibackend.pkibackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EntityInfo {
    private String commonName;
    private String surname;
    private String givenName;
    private String organization;
    private String organizationUnitName;
    private String countryCode;
    private String email;
    private Boolean isAccountNew;
}
