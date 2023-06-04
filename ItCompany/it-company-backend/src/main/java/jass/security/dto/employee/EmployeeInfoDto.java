package jass.security.dto.employee;

import jass.security.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInfoDto {
    private UUID employeeId;
    private String mail;
    private String role;
    private AddressDto address;
    private String name;
    private String phoneNumber;
    private String profession;
    private String surname;

    public EmployeeInfoDto(UUID employeeId,  String mail,  String role,  String country, String city, String street, String streetNumber ,  String name,  String phoneNumber,  String profession,  String surname){
        this.employeeId = employeeId;
        this.mail = mail;
        this.role = role;
        this.address = new AddressDto(country, city, street, streetNumber);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profession = profession;
        this.surname = surname;
    }
}

