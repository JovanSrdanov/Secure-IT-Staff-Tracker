package jass.security.controller;

import jass.security.dto.swengineer.AddSkillDto;
import jass.security.dto.swengineer.SeniorityDTO;
import jass.security.exception.NotFoundException;
import jass.security.model.Account;
import jass.security.model.Skill;
import jass.security.model.SoftwareEngineer;
import jass.security.service.interfaces.IAccountService;
import jass.security.service.interfaces.ISoftwareEngineerService;
import jass.security.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("skill")
    @PreAuthorize("hasAuthority('getAllSkillSwEngineer')")
    public ResponseEntity<?> GetAllSkills(Principal principal) {
        String swEngineerEmail = principal.getName();
        Account swEngineer = null;
        try {
            swEngineer = _accountService.findByEmail(swEngineerEmail);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This account does not exist!");
        }

        var skills = _softwareEngineerService.GetAllSkills(swEngineer.getEmployeeId());
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }

    @GetMapping("skill/{id}")
    @PreAuthorize("hasAuthority('getAllSkillSwEngineerById')")
    public ResponseEntity<?> GetAllSkillsById(@PathVariable("id") UUID swEngineerId) {

        var skills = _softwareEngineerService.GetAllSkills(swEngineerId);
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }


    @PostMapping("skill")
    @PreAuthorize("hasAuthority('addSkillSwEngineer')")
    public ResponseEntity<?> AddSkill(@RequestBody AddSkillDto dto, Principal principal) {
        String swEngineerEmail = principal.getName();
        Account swEngineer = null;
        try {
            swEngineer = _accountService.findByEmail(swEngineerEmail);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This account does not exist!");
        }

        try {
            var skill = _softwareEngineerService.AddSkill(swEngineer.getEmployeeId(), ObjectMapperUtils.map(dto, Skill.class));
            return new ResponseEntity<>(skill, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("skill/{id}")
    @PreAuthorize("hasAuthority('removeSkillSwEngineer')")
    public ResponseEntity<?> RemoveSkill(@PathVariable("id") UUID skillId, Principal principal) {
        String swEngineerEmail = principal.getName();
        Account swEngineer = null;
        try {
            swEngineer = _accountService.findByEmail(swEngineerEmail);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This account does not exist!");
        }

        try {
            _softwareEngineerService.RemoveSkill(swEngineer.getEmployeeId(), skillId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("seniority")
    @PreAuthorize("hasAuthority('getMySeniority')")
    public ResponseEntity<?> GetMySeniority(Principal principal) {
        try {
            String swEngineerEmail = principal.getName();
            Account acc = _accountService.findByEmail(swEngineerEmail);
            SoftwareEngineer softwareEngineer = _softwareEngineerService.findById(acc.getEmployeeId());

            return new ResponseEntity<>(new SeniorityDTO(softwareEngineer.getDateOfEmployment()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
