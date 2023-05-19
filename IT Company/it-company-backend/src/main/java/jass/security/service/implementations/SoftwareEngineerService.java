package jass.security.service.implementations;

import jass.security.exception.NotFoundException;
import jass.security.model.Skill;
import jass.security.model.SoftwareEngineer;
import jass.security.repository.ISkillRepository;
import jass.security.repository.ISoftwareEngineerRepository;
import jass.security.service.interfaces.ISoftwareEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class SoftwareEngineerService implements ISoftwareEngineerService {

    private final ISoftwareEngineerRepository _softwareEngineerRepository;
    private final ISkillRepository _skillRepository;

    @Autowired
    public SoftwareEngineerService(ISoftwareEngineerRepository _softwareEngineerRepository, ISkillRepository skillRepository) {
        this._softwareEngineerRepository = _softwareEngineerRepository;
        _skillRepository = skillRepository;
    }

    @Override
    public List<SoftwareEngineer> findAll() {
        return null;
    }

    @Override
    public SoftwareEngineer findById(UUID id) {
        return null;
    }

    @Override
    public SoftwareEngineer save(SoftwareEngineer entity) {
        return _softwareEngineerRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        _softwareEngineerRepository.deleteById(id);
    }

    @Override
    public Skill AddSkill(UUID swEngineerId, Skill skill) throws NotFoundException {
        var swEngineer = _softwareEngineerRepository.findById(swEngineerId);

        if(swEngineer.isEmpty()){
            throw new NotFoundException("Software engineer not found");
        }

        var updatedSwEngineer = swEngineer.get();
        skill.setId(UUID.randomUUID());
        updatedSwEngineer.getSkills().add(skill);
        _softwareEngineerRepository.save(updatedSwEngineer);
        return skill;
    }

    @Override
    public void RemoveSkill(UUID swEngineerId, UUID skillId) throws NotFoundException {
        var swEngineer = _softwareEngineerRepository.findById(swEngineerId);

        if(swEngineer.isEmpty()){
            throw new NotFoundException("Software engineer not found");
        }

        var updatedSwEngineer = swEngineer.get();
        updatedSwEngineer.getSkills().removeIf(skill -> skill.getId().equals(skillId));

        _softwareEngineerRepository.save(updatedSwEngineer);
        _skillRepository.deleteById(skillId);
    }
}
