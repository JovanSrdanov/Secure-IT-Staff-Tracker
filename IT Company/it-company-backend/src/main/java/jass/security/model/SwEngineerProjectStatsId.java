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
public class SwEngineerProjectStatsId implements Serializable {
    @Column(name = "sw_engineer_id")
    private UUID swEngineerId;
    @Column(name = "project_id")
    private UUID projectId;
}
