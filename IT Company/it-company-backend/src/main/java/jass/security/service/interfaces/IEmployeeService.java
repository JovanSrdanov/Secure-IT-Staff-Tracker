package jass.security.service.interfaces;

import jass.security.dto.employee.EmployeeInfoDto;

import java.util.List;
import java.util.UUID;

public interface IEmployeeService {
    List<EmployeeInfoDto> getAll();

    List<EmployeeInfoDto> getAllUnemployedEngineerOnProject(UUID projectId);

    List<EmployeeInfoDto> getAllUnemployedEngineerPRManager(UUID projectId);
}
