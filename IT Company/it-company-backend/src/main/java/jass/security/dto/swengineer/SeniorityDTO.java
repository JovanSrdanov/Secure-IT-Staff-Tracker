package jass.security.dto.swengineer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeniorityDTO {
    private Date dateOfEmployment;
    private String seniority;

    public SeniorityDTO(Date dateOfEmployment) {
        this.dateOfEmployment = dateOfEmployment;
        this.seniority = calculateSeniority(dateOfEmployment);
    }

    private String calculateSeniority(Date dateOfEmployment) {
        LocalDate currentDate = LocalDate.now();
        LocalDate employmentDate = dateOfEmployment.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(employmentDate, currentDate);
        int years = period.getYears();

        if (years >= 3) {
            return "SENIOR";
        } else if (years >= 2) {
            return "MEDIOR";
        } else {
            return "JUNIOR";
        }
    }
}
