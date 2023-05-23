package jass.security.dto.project;

import jass.security.model.DateRange;
import jass.security.model.SoftwareEngineer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SwEngineerProjectStatsDto {
    private SoftwareEngineer swEngineer;
    private String jobDescription;
    private DateRange workingPeriod;


    //Hibernate doesnt like lombok all args constructor
    public SwEngineerProjectStatsDto(SoftwareEngineer swEngineer, String jobDescription, DateRange workingPeriod) {
        this.swEngineer = swEngineer;
        this.jobDescription = jobDescription;
        this.workingPeriod = workingPeriod;
    }
}
