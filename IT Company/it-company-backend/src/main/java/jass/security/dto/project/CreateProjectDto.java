package jass.security.dto.project;

import jakarta.persistence.Entity;
import jass.security.model.DateRange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateProjectDto {
    private  String name;
    private DateRange duration;
}
