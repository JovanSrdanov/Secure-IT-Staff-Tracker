package jass.security.service.implementations;

import jass.security.dto.swengineer.SkillDto;
import jass.security.exception.NotFoundException;
import jass.security.model.Skill;
import jass.security.model.SoftwareEngineer;
import jass.security.repository.ISkillRepository;
import jass.security.repository.ISoftwareEngineerRepository;
import jass.security.service.interfaces.ISoftwareEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return _softwareEngineerRepository.findById(id).get();
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
    public List<SkillDto> GetAllSkills(UUID swEngineerId) {
        return _skillRepository.findBySoftwareEngineerId(swEngineerId);
    }

    @Override
    public SkillDto AddSkill(UUID swEngineerId, Skill skill) throws NotFoundException {
        var swEngineer = _softwareEngineerRepository.findById(swEngineerId);

        if (swEngineer.isEmpty()) {
            throw new NotFoundException("Software engineer not found");
        }

        skill.setId(UUID.randomUUID());
        skill.setSwEngineer(swEngineer.get());

        _skillRepository.save(skill);
        return new SkillDto(skill);
    }

    @Override
    @Transactional
    public void RemoveSkill(UUID swEngineerId, UUID skillId) throws NotFoundException {
        _skillRepository.deleteBySwEngineerId(skillId, swEngineerId);
    }
}
