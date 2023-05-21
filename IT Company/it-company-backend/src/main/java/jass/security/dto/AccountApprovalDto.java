package jass.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountApprovalDto {
    private String email;
    private String name;
    private String surname;
    private AddressDto address;
    private String phoneNumber;
    private String profession;
    private String role;
}
