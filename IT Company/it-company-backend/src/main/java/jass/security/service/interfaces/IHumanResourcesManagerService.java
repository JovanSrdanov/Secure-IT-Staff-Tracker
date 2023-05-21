package jass.security.service.interfaces;

import jass.security.dto.employee.EmployeeProfileInfoDto;
import jass.security.exception.NotFoundException;
import jass.security.model.HrManager;

import java.util.UUID;

public interface IHumanResourcesManagerService extends ICrudService<HrManager>{
    HrManager update(UUID id, EmployeeProfileInfoDto dto) throws NotFoundException;
}
