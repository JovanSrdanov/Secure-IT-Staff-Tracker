package jass.security.dto;

import jass.security.model.Address;
import jass.security.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAccountDto {
    String email;
    String password;
    String role;

    String name;
    String surname;
    AddressDto address;
    String phoneNumber;
    String profession;
}
