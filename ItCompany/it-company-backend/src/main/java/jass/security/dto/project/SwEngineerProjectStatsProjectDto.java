package jass.security.dto.project;


import jass.security.model.DateRange;
import jass.security.model.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SwEngineerProjectStatsProjectDto {
    private Project project;
    private String jobDescription;
    private DateRange workingPeriod;

    public SwEngineerProjectStatsProjectDto(Project project, String jobDescription, DateRange workingPeriod) {
        this.project = project;
        this.jobDescription = jobDescription;
        this.workingPeriod = workingPeriod;
    }
}
