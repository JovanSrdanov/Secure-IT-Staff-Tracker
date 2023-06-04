package jass.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    @NotBlank(message = "The country is required.")
    private String country;
    @NotBlank(message = "The city is required.")
    private String city;
    @NotBlank(message = "The street is required.")
    private String street;
    @NotBlank(message = "The street number is required.")
    private String streetNumber;
}
