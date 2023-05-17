package jass.security.dto;

import jass.security.model.Address;
import jass.security.model.Role;

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
