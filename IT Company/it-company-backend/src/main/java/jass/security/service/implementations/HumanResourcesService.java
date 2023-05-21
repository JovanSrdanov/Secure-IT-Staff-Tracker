package jass.security.service.implementations;

import jass.security.dto.employee.EmployeeProfileInfoDto;
import jass.security.exception.NotFoundException;
import jass.security.model.HrManager;
import jass.security.repository.IHrManagerRepository;
import jass.security.service.interfaces.IHumanResourcesManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class HumanResourcesService implements IHumanResourcesManagerService {
    private final IHrManagerRepository _humanResourcesManagerRepository;

    @Autowired
    public HumanResourcesService(IHrManagerRepository humanResourcesManagerRepository) {
        _humanResourcesManagerRepository = humanResourcesManagerRepository;
    }

    @Override
    public List<HrManager> findAll() {
        return null;
    }

    @Override
    public HrManager findById(UUID id) throws NotFoundException {
        var hrmanager = _humanResourcesManagerRepository.findById(id);
        if (hrmanager.isEmpty()) {
            throw new NotFoundException("engineer not found");
        }
        return hrmanager.get();
    }

    @Override
    public HrManager save(HrManager entity) {
        return _humanResourcesManagerRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        _humanResourcesManagerRepository.deleteById(id);
    }

    @Override
    public HrManager update(UUID id, EmployeeProfileInfoDto dto) throws NotFoundException {
        var oldEmployee = findById(id);
        oldEmployee.update(dto);

        return _humanResourcesManagerRepository.save(oldEmployee);
    }
}
