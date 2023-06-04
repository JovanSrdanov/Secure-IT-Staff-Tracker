package jass.security.dto.swengineer;

import jass.security.model.Skill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private UUID id;
    private String name;
    private int level;

    public SkillDto(Skill skill){
        this.id = skill.getId();
        this.name = skill.getName();
        this.level = skill.getLevel();
    }
}
