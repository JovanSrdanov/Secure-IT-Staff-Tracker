package jass.security.controller;

import jass.security.dto.employee.EmployeeProfileInfoDto;
import jass.security.exception.NotFoundException;
import jass.security.model.Account;
import jass.security.service.interfaces.IAccountService;
import jass.security.service.interfaces.IEmployeeService;
import jass.security.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("employee")
public class EmployeeController {

    private final IEmployeeService _employeeService;

    @Autowired
    public EmployeeController(IEmployeeService employeeService) {
        _employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('getAllEmployee')")
    public ResponseEntity<?> getAll(){
       var employees = _employeeService.getAll();
       return new ResponseEntity(employees, HttpStatus.OK);
    }
    @GetMapping("/unemployed/{id}")
    @PreAuthorize("hasAuthority('getAllUnemployedOnProjectEmployee')")
    public ResponseEntity<?> getAllUnemployedOnProject(@PathVariable("id") UUID projectId){
        var employees = _employeeService.getAllUnemployedOnProject(projectId);
        return new ResponseEntity(employees, HttpStatus.OK);
    }
}
