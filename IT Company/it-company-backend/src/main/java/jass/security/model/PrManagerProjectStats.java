package jass.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class PrManagerProjectStats {
    @Id
    private PrManagerProjectStatsId id;
    @Column(nullable = false)
    private DateRange workingPeriod;


    @ManyToOne
    @MapsId("prManagerId")
    @JoinColumn(name = "pr_manager_id")
    private ProjectManager projectManager;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project _id")
    private Project project;
}
