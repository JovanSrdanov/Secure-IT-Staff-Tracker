package jass.security.dto.employee;

import jass.security.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileInfoDto {
    private String name;
    private String surname;
    private String phoneNumber;
    private String profession;
    private AddressDto address;
}
