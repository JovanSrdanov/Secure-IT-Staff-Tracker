package jass.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class SoftwareEngineer extends Employee {
    @Column(nullable = false)
    private Date dateOfEmployment;
    @OneToMany
    private ArrayList<Skill> skills;
    @OneToOne
    private Cv cv;
    @OneToMany(mappedBy = "softwareEngineer")
    private ArrayList<EngineerProjectStats> projectStats;
}
