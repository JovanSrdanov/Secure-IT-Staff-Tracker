package jass.security.service.implementations;

import jass.security.dto.employee.EmployeeInfoDto;
import jass.security.dto.employee.EmployeeProfileInfoDto;
import jass.security.exception.NotFoundException;
import jass.security.model.Employee;
import jass.security.repository.IEmployeeRepository;
import jass.security.service.interfaces.IEmployeeService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class EmployeeService implements IEmployeeService {
    private final IEmployeeRepository _employeeRepository;

    public EmployeeService(IEmployeeRepository employeeRepository) {
        _employeeRepository = employeeRepository;
    }

    @Override
    public List<EmployeeInfoDto> getAll() {
        return _employeeRepository.getAll();
    }

    @Override
    public List<EmployeeInfoDto> getAllUnemployedOnProject(UUID projectId) {
        var employedIds = _employeeRepository.getAllEmployedOnProjectId(projectId);
        if(employedIds.isEmpty())
        {
            //HQL cant work with empty list
            employedIds.add(new UUID(0L, 0L));
        }
        return _employeeRepository.getOppositeEmployeeGroup(employedIds);
    }
}
