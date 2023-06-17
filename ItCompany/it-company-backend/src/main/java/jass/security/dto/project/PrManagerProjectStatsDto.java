package jass.security.dto.project;

import jass.security.model.DateRange;
import jass.security.model.ProjectManager;
import jass.security.model.SoftwareEngineer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PrManagerProjectStatsDto {
    private ProjectManager prManager;
    private DateRange workingPeriod;

    public PrManagerProjectStatsDto(ProjectManager prManager, DateRange workingPeriod) {
        this.prManager = prManager;
        this.workingPeriod = workingPeriod;
    }
}
