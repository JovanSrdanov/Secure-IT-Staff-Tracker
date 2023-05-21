package jass.security.service.interfaces;

import jass.security.dto.swengineer.SkillDto;
import jass.security.exception.NotFoundException;
import jass.security.model.Skill;
import jass.security.model.SoftwareEngineer;

import java.util.List;
import java.util.UUID;

public interface ISoftwareEngineerService extends ICrudService<SoftwareEngineer> {
    List<SkillDto> GetAllSkills(UUID swEngineerId);
    SkillDto AddSkill(UUID swEngineerId, Skill skill) throws NotFoundException;
    void RemoveSkill(UUID swEngineerId, UUID skillId) throws NotFoundException;
}
