package jass.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jass.security.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class Address {
    @Id
    private UUID id;


    @ColumnTransformer( read = "pgp_sym_decrypt( country, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private String country;

    @ColumnTransformer( read = "pgp_sym_decrypt( city, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private String city;


    @ColumnTransformer( read = "pgp_sym_decrypt( street, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private String street;


    @ColumnTransformer( read = "pgp_sym_decrypt( street_number, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private String streetNumber;

    public void update(AddressDto dto) {
        setCity(dto.getCity());
        setStreetNumber(dto.getStreetNumber());
        setCountry(dto.getCountry());
        setStreet(dto.getStreet());
    }
}
