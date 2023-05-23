package jass.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jass.security.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class Address {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String streetNumber;

    public void update(AddressDto dto) {
        setCity(dto.getCity());
        setStreetNumber(dto.getStreetNumber());
        setCountry(dto.getCountry());
        setStreet(dto.getStreet());
    }
}
