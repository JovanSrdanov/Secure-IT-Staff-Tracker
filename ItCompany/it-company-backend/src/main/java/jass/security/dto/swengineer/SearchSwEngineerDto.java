package jass.security.dto.swengineer;

import jass.security.model.DateRange;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchSwEngineerDto {
    private String email;
    private String name;
    private String surname;
    private DateRange employmentDateRange;
}
