package jass.security.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAccountDto {
    @Email
    String email;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$", message = "Password must contain at least: minimum 8 charachters, one uppercase letter, one lowercase letter, one special charachter and one number")
    String password;
    @NotBlank(message = "The role is required.")
    String role;
    @NotBlank(message = "The name is required.")
    String name;
    @NotBlank(message = "The surname is required.")
    String surname;
    @Valid
    @NotNull
    AddressDto address;
    @NotBlank(message = "The address is required.")
    String phoneNumber;
    @NotBlank(message = "The profession is required.")
    String profession;
}
