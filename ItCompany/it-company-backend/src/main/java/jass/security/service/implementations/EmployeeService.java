package jass.security.service.implementations;

import jass.security.dto.employee.EmployeeInfoDto;
import jass.security.model.Employee;
import jass.security.repository.IEmployeeRepository;
import jass.security.service.interfaces.IAccountService;
import jass.security.service.interfaces.IEmployeeService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Primary
public class EmployeeService implements IEmployeeService {
    private final IEmployeeRepository _employeeRepository;
    private final IAccountService _accountService;

    public EmployeeService(IEmployeeRepository employeeRepository, IAccountService accountService) {
        _employeeRepository = employeeRepository;
        _accountService = accountService;
    }

    @Override
    public List<EmployeeInfoDto> getAll() {
        return _employeeRepository.getAll();
    }

    @Override
    public List<EmployeeInfoDto> getAllUnemployedEngineerOnProject(UUID projectId) {
        var employedIds = _employeeRepository.getAllEmployedEngineerOnProjectId(projectId);
        if (employedIds.isEmpty()) {
            //HQL cant work with empty list
            employedIds.add(new UUID(0L, 0L));
        }
        return _employeeRepository.getOppositeEmployeeEngineerGroup(employedIds);
    }

    @Override
    public List<EmployeeInfoDto> getAllUnemployedEngineerPRManager(UUID projectId) {
        var employedIds = _employeeRepository.getAllEmployedPrManagerOnProjectId(projectId);
        if (employedIds.isEmpty()) {
            //HQL cant work with empty list
            employedIds.add(new UUID(0L, 0L));
        }
        return _employeeRepository.getOppositeEmployeePrManagerGroup(employedIds);
    }
}
