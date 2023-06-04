package jass.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class SwEngineerProjectStats implements Serializable {
    @Id
    private SwEngineerProjectStatsId id;

    @ColumnTransformer( read = "pgp_sym_decrypt(job_description, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(nullable = false, columnDefinition = "bytea")
    private String jobDescription;

    @Column(nullable = false)
    private DateRange workingPeriod;


    @ManyToOne
    @MapsId("swEngineerId")
    @JoinColumn(name = "sw_engineer_id")
    private SoftwareEngineer softwareEngineer;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project _id")
    private Project project;
}
