package jass.security.dto;

import jakarta.validation.Valid;
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
public class RegisterAdminAccountDto {
    @Pattern(regexp = "^[\\w.+/-]+@[a-zA-Z_-]+?\\.[a-zA-Z]{2,3}$", message = "Not a valid email address")
    String email;
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
