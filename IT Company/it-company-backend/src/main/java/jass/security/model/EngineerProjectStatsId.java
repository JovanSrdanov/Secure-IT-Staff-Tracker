package jass.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class EngineerProjectStatsId implements Serializable {
    @Column(name = "sw_engineer_id")
    private UUID swEngineerId;
    @Column(name = "project_id")
    private UUID projectId;
}
