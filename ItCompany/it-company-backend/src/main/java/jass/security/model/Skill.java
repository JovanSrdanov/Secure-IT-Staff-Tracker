package jass.security.model;


import jakarta.persistence.*;
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
public class Skill {
    @Id
    private UUID id;

    //Skripte se pokrecu za vreme reada i writea
   @ColumnTransformer( read = "pgp_sym_decrypt( name, current_setting('encrypt.key') )",
           write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private String name;


   //Ima problema sa konvertovanjem integera (pratio sam errore dok nisam uboo, ne znam kako)
    @ColumnTransformer( read = "pgp_sym_decrypt( CAST(CAST(level as text) AS bytea), current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( CAST(? as text), current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private int level;
    @ManyToOne(fetch = FetchType.LAZY)
    private SoftwareEngineer swEngineer;
}
