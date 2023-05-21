package jass.security.controller;

import jakarta.validation.Valid;
import jass.security.dto.employee.EmployeeProfileInfoDto;
import jass.security.exception.NotFoundException;
import jass.security.model.*;
import jass.security.service.interfaces.*;
import jass.security.utils.ObjectMapperUtils;
import jass.security.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("employee")
public class EmployeeController {

    private final IEmployeeService _employeeService;
    private final IAccountService _accountService;
    private final ISoftwareEngineerService _softwareEngineerService;
    private final IAdministratorService _administratorService;
    private final IProjectManagerService _projectManagerService;
    private final IHumanResourcesManagerService _humanResourcesManagerService;

    @Autowired
    public EmployeeController(IEmployeeService employeeService, IAccountService accountService,
                              ISoftwareEngineerService softwareEngineerService,
                              IAdministratorService administratorService,
                              IProjectManagerService projectManagerService,
                              IHumanResourcesManagerService humanResourcesManagerService) {
        _employeeService = employeeService;
        _accountService = accountService;
        _softwareEngineerService = softwareEngineerService;
        _administratorService = administratorService;
        _projectManagerService = projectManagerService;
        _humanResourcesManagerService = humanResourcesManagerService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('getAllEmployee')")
    public ResponseEntity<?> getAll() {
        var employees = _employeeService.getAll();
        return new ResponseEntity(employees, HttpStatus.OK);
    }

    @GetMapping("/unemployed-engineer/{id}")
    @PreAuthorize("hasAuthority('getAllUnemployedOnProjectEngineer')")
    public ResponseEntity<?> getAllUnemployedOnProjectEngineer(@PathVariable("id") UUID projectId) {
        var employees = _employeeService.getAllUnemployedEngineerOnProject(projectId);
        return new ResponseEntity(employees, HttpStatus.OK);
    }

    @GetMapping("/unemployed-pr-manager/{id}")
    @PreAuthorize("hasAuthority('getAllUnemployedOnProjectPRManager')")
    public ResponseEntity<?> getAllUnemployedOnProjectPrManager(@PathVariable("id") UUID projectId) {
        var employees = _employeeService.getAllUnemployedEngineerPRManager(projectId);
        return new ResponseEntity(employees, HttpStatus.OK);
    }


    @GetMapping("/logged-in-info")
    @PreAuthorize("hasAuthority('getLoggedInInfo')")
    public ResponseEntity<?> getLoggedInInfo(Principal principal) {
        String employeeEmail = principal.getName();
        Account employeeCredentials = _accountService.findByEmail(employeeEmail);
        String role = TokenUtils.extractRoleFromAuthenticationHeader((Authentication) principal);

        try {
            switch (role) {
                case "ROLE_ENGINEER" -> {
                    SoftwareEngineer engineerInfo = _softwareEngineerService.findById(employeeCredentials.getEmployeeId());
                    return new ResponseEntity<>(
                            ObjectMapperUtils.map(engineerInfo, EmployeeProfileInfoDto.class),
                            HttpStatus.OK
                    );
                }
                case "ROLE_ADMIN" -> {
                    Administrator adminInfo = _administratorService.findById(employeeCredentials.getEmployeeId());
                    return new ResponseEntity<>(
                            ObjectMapperUtils.map(adminInfo, EmployeeProfileInfoDto.class),
                            HttpStatus.OK
                    );
                }
                case "ROLE_PROJECT_MANAGER" -> {
                    ProjectManager prManagerInfo = _projectManagerService.findById(employeeCredentials.getEmployeeId());
                    return new ResponseEntity<>(
                            ObjectMapperUtils.map(prManagerInfo, EmployeeProfileInfoDto.class),
                            HttpStatus.OK
                    );
                }
                case "ROLE_HR_MANAGER" -> {
                    HrManager hrManagerInfo = _humanResourcesManagerService.findById(employeeCredentials.getEmployeeId());
                    return new ResponseEntity<>(
                            ObjectMapperUtils.map(hrManagerInfo, EmployeeProfileInfoDto.class),
                            HttpStatus.OK
                    );
                }
                default -> {
                    return new ResponseEntity<>(
                            "employee with that role not found",
                            HttpStatus.BAD_REQUEST
                    );
                }
            }
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    "employee not found",
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @PutMapping("/logged-in-info")
    @PreAuthorize("hasAuthority('updateLoggedInInfo')")
    public ResponseEntity<?> updateLoggedInInfo(@Valid @RequestBody EmployeeProfileInfoDto dto, Principal principal) {
        String employeeEmail = principal.getName();
        Account employeeCredentials = _accountService.findByEmail(employeeEmail);
        String role = TokenUtils.extractRoleFromAuthenticationHeader((Authentication) principal);

        try {
            switch (role) {
                case "ROLE_ENGINEER" -> {
                    var updatedInfo = _softwareEngineerService.update(employeeCredentials.getEmployeeId(), dto);
                    return new ResponseEntity<>(
                            ObjectMapperUtils.map(updatedInfo, EmployeeProfileInfoDto.class),
                            HttpStatus.OK
                    );
                }
                case "ROLE_ADMIN" -> {
                    var updatedInfo = _administratorService.update(employeeCredentials.getEmployeeId(), dto);
                    return new ResponseEntity<>(
                            ObjectMapperUtils.map(updatedInfo, EmployeeProfileInfoDto.class),
                            HttpStatus.OK
                    );
                }
                case "ROLE_PROJECT_MANAGER" -> {
                    var updatedInfo = _projectManagerService.update(employeeCredentials.getEmployeeId(), dto);
                    return new ResponseEntity<>(
                            ObjectMapperUtils.map(updatedInfo, EmployeeProfileInfoDto.class),
                            HttpStatus.OK
                    );
                }
                case "ROLE_HR_MANAGER" -> {
                    var updatedInfo = _humanResourcesManagerService.update(employeeCredentials.getEmployeeId(), dto);
                    return new ResponseEntity<>(
                            ObjectMapperUtils.map(updatedInfo, EmployeeProfileInfoDto.class),
                            HttpStatus.OK
                    );
                }
                default -> {
                    return new ResponseEntity<>(
                            "employee with that role not found",
                            HttpStatus.BAD_REQUEST
                    );
                }
            }
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    "cannot find employee info",
                    HttpStatus.NOT_FOUND
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    "failed to update info, argument is null",
                    HttpStatus.BAD_REQUEST
            );
        } catch (OptimisticLockingFailureException e) {
            return new ResponseEntity<>(
                    "failed to update info due to optimistic locking",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
