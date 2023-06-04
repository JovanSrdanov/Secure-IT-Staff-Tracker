package jass.security.service.implementations;

import jass.security.model.HrManager;
import jass.security.repository.IHrManagerRepository;
import jass.security.service.interfaces.IHrManagerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public class HrManagerService implements IHrManagerService {

    private final IHrManagerRepository managerRepository;

    @Autowired
    public HrManagerService(IHrManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public List<HrManager> findAll() {
        return null;
    }

    @Override
    public HrManager findById(UUID id) {
        return null;
    }

    @Override
    public HrManager save(HrManager entity) {
        return managerRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        managerRepository.deleteById(id);
    }
}
