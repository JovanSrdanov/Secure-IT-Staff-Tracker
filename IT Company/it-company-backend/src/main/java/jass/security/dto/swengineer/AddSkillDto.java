package jass.security.dto.swengineer;

import jakarta.persistence.Column;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddSkillDto {
    private String name;
    private int level;
}
