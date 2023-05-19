package jass.security.service.interfaces;

import jass.security.exception.NotFoundException;
import jass.security.model.Skill;
import jass.security.model.SoftwareEngineer;

import java.util.UUID;

public interface ISoftwareEngineerService extends ICrudService<SoftwareEngineer> {
    Skill AddSkill(UUID swEngineerId, Skill skill) throws NotFoundException;
    void RemoveSkill(UUID swEngineerId, UUID skillId) throws NotFoundException;
}
