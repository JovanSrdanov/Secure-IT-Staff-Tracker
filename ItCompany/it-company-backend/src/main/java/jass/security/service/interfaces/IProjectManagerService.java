package jass.security.service.interfaces;

import jass.security.dto.employee.EmployeeProfileInfoDto;
import jass.security.exception.NotFoundException;
import jass.security.model.ProjectManager;

import java.util.UUID;

public interface IProjectManagerService extends ICrudService<ProjectManager>{
    ProjectManager update(UUID id, EmployeeProfileInfoDto dto) throws NotFoundException;
    boolean isSuperior(UUID prManagerId, UUID engineerId);
}
