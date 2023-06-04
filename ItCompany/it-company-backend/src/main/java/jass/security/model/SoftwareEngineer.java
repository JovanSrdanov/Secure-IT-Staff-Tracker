package jass.security.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class SoftwareEngineer extends Employee {
    @ColumnTransformer( read = "pgp_sym_decrypt(date_of_employment , current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(columnDefinition = "bytea")
    private Date dateOfEmployment;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "swEngineer")
    private Set<Skill> skills;
    @OneToOne
    private Cv cv;
}
