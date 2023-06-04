package jass.security.service.implementations;

import jass.security.dto.employee.EmployeeProfileInfoDto;
import jass.security.exception.NotFoundException;
import jass.security.model.Administrator;
import jass.security.model.SoftwareEngineer;
import jass.security.repository.IAdministratorRepository;
import jass.security.service.interfaces.IAdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class AdministratorService implements IAdministratorService {
    private final IAdministratorRepository _administratorRepository;

    @Autowired
    public AdministratorService(IAdministratorRepository administratorRepository) {
        _administratorRepository = administratorRepository;
    }

    @Override
    public List<Administrator> findAll() {
        return _administratorRepository.findAll();
    }

    @Override
    public Administrator findById(UUID id) throws NotFoundException {
        var engineer = _administratorRepository.findById(id);
        if (engineer.isEmpty()) {
            throw new NotFoundException("engineer not found");
        }
        return engineer.get();
    }

    @Override
    public Administrator save(Administrator entity) {
        return _administratorRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        _administratorRepository.deleteById(id);
    }

    @Override
    public Administrator update(UUID id, EmployeeProfileInfoDto dto) throws NotFoundException {
        var oldEmployee = findById(id);
        oldEmployee.update(dto);

        return _administratorRepository.save(oldEmployee);
    }
}
