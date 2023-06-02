package jass.security.model;

import jakarta.persistence.*;
import jass.security.dto.project.UpdateProjectDto;
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
public class Project {
    @Id
    private UUID id;

    @ColumnTransformer( read = "pgp_sym_decrypt( name, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private String name;

    @Column(nullable = false)
    private DateRange duration;

    public void update(UpdateProjectDto dto) {
        setName(dto.getName());
        this.duration.setEndDate(dto.getEndDate());
    }
}