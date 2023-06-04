package jass.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PrManagerProjectStatsId implements Serializable {

    @Column(name = "pr_manager_id")
    private UUID prManagerId;
    @Column(name = "project_id")
    private UUID projectId;
}
