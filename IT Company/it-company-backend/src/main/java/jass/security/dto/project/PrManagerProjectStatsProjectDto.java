package jass.security.dto.project;

import jass.security.model.DateRange;
import jass.security.model.Project;
import jass.security.model.ProjectManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PrManagerProjectStatsProjectDto {
    private Project project;
    private DateRange workingPeriod;

    public PrManagerProjectStatsProjectDto(Project project, DateRange workingPeriod) {
        this.project = project;
        this.workingPeriod = workingPeriod;
    }
}
