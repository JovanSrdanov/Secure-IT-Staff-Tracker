package jass.security.controller;

import jass.security.service.interfaces.IEmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("employee")
public class EmployeeController {

    private final IEmployeeService _employeeService;

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