package jass.security.service.interfaces;

import jass.security.dto.employee.EmployeeProfileInfoDto;
import jass.security.exception.NotFoundException;
import jass.security.model.Administrator;
import jass.security.model.SoftwareEngineer;

import java.util.UUID;

public interface IAdministratorService extends ICrudService<Administrator>{
    Administrator update(UUID id, EmployeeProfileInfoDto dto) throws NotFoundException;
}
