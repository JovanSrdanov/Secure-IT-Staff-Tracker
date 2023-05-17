package jass.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class EngineerProjectStats implements Serializable {
    @Id
    private EngineerProjectStatsId id;
    @Column(nullable = false)
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









