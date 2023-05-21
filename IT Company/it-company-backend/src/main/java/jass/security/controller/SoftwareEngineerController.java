package jass.security.controller;

import jass.security.dto.employee.EmployeeProfileInfoDto;
import jass.security.dto.swengineer.AddSkillDto;
import jass.security.exception.NotFoundException;
import jass.security.model.Account;
import jass.security.model.Skill;
import jass.security.model.SoftwareEngineer;
import jass.security.service.interfaces.IAccountService;
import jass.security.service.interfaces.IEmployeeService;
import jass.security.service.interfaces.ISoftwareEngineerService;
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
@RequestMapping("sw-engineer")
public class SoftwareEngineerController {

    private final IAccountService _accountService;
    private final ISoftwareEngineerService _softwareEngineerService;

    @Autowired
    public SoftwareEngineerController(IAccountService _accountService,
                                      ISoftwareEngineerService softwareEngineerService) {
        this._accountService = _accountService;
        _softwareEngineerService = softwareEngineerService;
    }

    @GetMapping("/logged-in-info")
    @PreAuthorize("hasAuthority('getLoggedInInfo')")
    public ResponseEntity<?> getLoggedInInfo(Principal principal) {
        String employeeEmail = principal.getName();
        Account employeeCredentials = _accountService.findByEmail(employeeEmail);

        try {
            SoftwareEngineer employeeInfo = _softwareEngineerService.findById(employeeCredentials.getEmployeeId());
            return new ResponseEntity<>(
                    ObjectMapperUtils.map(employeeInfo, EmployeeProfileInfoDto.class),
                    HttpStatus.OK
            );
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    "engineer not found",
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @PutMapping("/logged-in-info")
    @PreAuthorize("hasAuthority('updateLoggedInInfo')")
    public ResponseEntity<?> updateLoggedInInfo(@RequestBody EmployeeProfileInfoDto dto, Principal principal) {
        String employeeEmail = principal.getName();
        Account employeeCredentials = _accountService.findByEmail(employeeEmail);

        try {
            var updatedInfo = _softwareEngineerService.update(employeeCredentials.getEmployeeId(), dto);
            return new ResponseEntity<>(
                    ObjectMapperUtils.map(updatedInfo, EmployeeProfileInfoDto.class),
                    HttpStatus.OK
            );
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

    @GetMapping("skill")
    @PreAuthorize("hasAuthority('getAllSkillSwEngineer')")
    public ResponseEntity<?> GetAllSkills(Principal principal){
        String swEngineerEmail = principal.getName();
        Account swEngineer = _accountService.findByEmail(swEngineerEmail);

        var skills = _softwareEngineerService.GetAllSkills(swEngineer.getEmployeeId());
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }

    @PostMapping("skill")
    @PreAuthorize("hasAuthority('addSkillSwEngineer')")
    public ResponseEntity<?> AddSkill(@RequestBody AddSkillDto dto, Principal principal){
        String swEngineerEmail = principal.getName();
        Account swEngineer = _accountService.findByEmail(swEngineerEmail);

        try {
            var skill = _softwareEngineerService.AddSkill(swEngineer.getEmployeeId(), ObjectMapperUtils.map(dto, Skill.class));
            return new ResponseEntity<>(skill, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("skill/{id}")
    @PreAuthorize("hasAuthority('removeSkillSwEngineer')")
    public ResponseEntity<?> RemoveSkill(@PathVariable("id") UUID skillId, Principal principal){
        String swEngineerEmail = principal.getName();
        Account swEngineer = _accountService.findByEmail(swEngineerEmail);

        try {
            _softwareEngineerService.RemoveSkill(swEngineer.getEmployeeId(), skillId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
