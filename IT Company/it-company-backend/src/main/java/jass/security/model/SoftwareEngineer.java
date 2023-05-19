package jass.security.model;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class SoftwareEngineer extends Employee {
    private Date dateOfEmployment;
    @OneToMany
    private Set<Skill> skills;
    @OneToOne
    private Cv cv;
}
