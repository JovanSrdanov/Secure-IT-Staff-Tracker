package jass.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jass.security.dto.swengineer.AddSkillDto;
import jass.security.dto.swengineer.SeniorityDTO;
import jass.security.exception.NotFoundException;
import jass.security.model.Account;
import jass.security.model.Skill;
import jass.security.model.SoftwareEngineer;
import jass.security.service.interfaces.IAccountService;
import jass.security.service.interfaces.ISoftwareEngineerService;
import jass.security.utils.IPUtils;
import jass.security.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(SoftwareEngineerController.class);

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
        Account swEngineer = _accountService.findByEmail(swEngineerEmail);

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
    public ResponseEntity<?> AddSkill(@RequestBody AddSkillDto dto, Principal principal, HttpServletRequest request) {
        String swEngineerEmail = principal.getName();
        Account swEngineer = _accountService.findByEmail(swEngineerEmail);

        try {
            var skill = _softwareEngineerService.AddSkill(swEngineer.getEmployeeId(), ObjectMapperUtils.map(dto, Skill.class));
            logger.info("Successful attempt to add a skill to an engineer with an account ID: " +
                    swEngineer.getId() + ", from an IP: " + IPUtils.getIPAddressFromHttpRequest(request));

            return new ResponseEntity<>(skill, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            logger.warn("Failed attempt to add a skill to " +
                            "an engineer, from IP: " + IPUtils.getIPAddressFromHttpRequest(request),
                    " reason: given engineer email does not exist");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("skill/{id}")
    @PreAuthorize("hasAuthority('removeSkillSwEngineer')")
    public ResponseEntity<?> RemoveSkill(@PathVariable("id") UUID skillId, Principal principal,
                                         HttpServletRequest request) {
        String swEngineerEmail = principal.getName();
        Account swEngineer = _accountService.findByEmail(swEngineerEmail);

        try {
            _softwareEngineerService.RemoveSkill(swEngineer.getEmployeeId(), skillId);
            logger.info("Successful attempt to remove a skill from an engineer with an account ID: " +
                    swEngineer.getId() + ", from an IP: " + IPUtils.getIPAddressFromHttpRequest(request));

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotFoundException e) {
            logger.warn("Failed attempt to remove a skill from " +
                            "an engineer, from IP: " + IPUtils.getIPAddressFromHttpRequest(request),
                    " reason: given engineer email does not exist");

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
