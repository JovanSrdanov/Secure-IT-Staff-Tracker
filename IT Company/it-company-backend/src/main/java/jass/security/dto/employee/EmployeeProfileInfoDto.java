package jass.security.dto.employee;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jass.security.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileInfoDto {
    @NotBlank(message = "The name is required.")
    private String name;
    @NotBlank(message = "The surname is required.")
    private String surname;
    @NotBlank(message = "The phoneNumber is required.")
    private String phoneNumber;
    @NotBlank(message = "The profession is required.")
    private String profession;
    @Valid
    @NotNull
    private AddressDto address;
}
