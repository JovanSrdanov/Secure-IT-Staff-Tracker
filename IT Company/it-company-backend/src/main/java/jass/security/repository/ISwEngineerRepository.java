package jass.security.repository;

import jass.security.dto.employee.EmployeeProfileInfoDto;
import jass.security.exception.NotFoundException;
import jass.security.model.SoftwareEngineer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ISwEngineerRepository extends JpaRepository<SoftwareEngineer, UUID> {
}
