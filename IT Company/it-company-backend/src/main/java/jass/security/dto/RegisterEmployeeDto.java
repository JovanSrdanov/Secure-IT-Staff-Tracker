package jass.security.dto;

import jass.security.model.Address;
import jass.security.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record RegisterEmployeeDto(
        String email,
        String password,
        Role role,
        String name,
        String surname,
        Address address,
        String phoneNumber,
        String profession

) { }
