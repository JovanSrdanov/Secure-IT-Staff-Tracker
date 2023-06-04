package jass.security.model;

import jakarta.persistence.*;
import jass.security.dto.employee.EmployeeProfileInfoDto;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@MappedSuperclass
public class Employee {
    @Id
    private UUID id;


    @ColumnTransformer( read = "pgp_sym_decrypt( name, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private String name;

    @ColumnTransformer( read = "pgp_sym_decrypt( surname, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private String surname;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Address address;

    @ColumnTransformer( read = "pgp_sym_decrypt( phone_number, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private String phoneNumber;


    @ColumnTransformer( read = "pgp_sym_decrypt( profession, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private String profession; //zvanje

    public void update(EmployeeProfileInfoDto dto) {
        setName(dto.getName());
        setSurname(dto.getSurname());
        setPhoneNumber(dto.getPhoneNumber());
        setProfession(dto.getProfession());
        this.address.update(dto.getAddress());
    }
}





















