package jass.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jass.security.dto.swengineer.AddSkillDto;
import jass.security.dto.swengineer.SearchSwEngineerDto;
import jass.security.dto.swengineer.SearchSwResponseDto;
import jass.security.dto.swengineer.SeniorityDTO;
import jass.security.exception.NotFoundException;
import jass.security.model.Account;
import jass.security.model.Skill;
import jass.security.model.SoftwareEngineer;
import jass.security.service.interfaces.IAccountService;
import jass.security.service.interfaces.ICvService;
import jass.security.service.interfaces.IProjectManagerService;
import jass.security.service.interfaces.ISoftwareEngineerService;
import jass.security.utils.IPUtils;
import jass.security.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("sw-engineer")
public class SoftwareEngineerController {

    private final IAccountService _accountService;
    private final ISoftwareEngineerService _softwareEngineerService;
    private static final Logger logger = LoggerFactory.getLogger(SoftwareEngineerController.class);
    private final IProjectManagerService projectManagerService;
    private final ICvService cvService;

    @Autowired
    public SoftwareEngineerController(IAccountService _accountService,
                                      ISoftwareEngineerService softwareEngineerService, IProjectManagerService projectManagerService, ICvService cvService) {
        this._accountService = _accountService;
        _softwareEngineerService = softwareEngineerService;
        this.projectManagerService = projectManagerService;
        this.cvService = cvService;
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
    public ResponseEntity<?> AddSkill(@RequestBody AddSkillDto dto, Principal principal, HttpServletRequest request) {
        String swEngineerEmail = principal.getName();
        Account swEngineer = null;
        try {
            swEngineer = _accountService.findByEmail(swEngineerEmail);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This account does not exist!");
        }

        try {
            var skill = _softwareEngineerService.AddSkill(swEngineer.getEmployeeId(), ObjectMapperUtils.map(dto, Skill.class));
            logger.info("Successful attempt to add a skill to an engineer with an account ID: " +
                    swEngineer.getId() + ", from an IP: " + IPUtils.getIPAddressFromHttpRequest(request));

            return new ResponseEntity<>(skill, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            logger.warn("Failed attempt to add a skill to " +
                            "an engineer, from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    " reason: given engineer email does not exist");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("skill/{id}")
    @PreAuthorize("hasAuthority('removeSkillSwEngineer')")
    public ResponseEntity<?> RemoveSkill(@PathVariable("id") UUID skillId, Principal principal,
                                         HttpServletRequest request) {
        String swEngineerEmail = principal.getName();
        Account swEngineer = null;
        try {
            swEngineer = _accountService.findByEmail(swEngineerEmail);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This account does not exist!");
        }

        try {
            _softwareEngineerService.RemoveSkill(swEngineer.getEmployeeId(), skillId);
            logger.info("Successful attempt to remove a skill from an engineer with an account ID: " +
                    swEngineer.getId() + ", from an IP: " + IPUtils.getIPAddressFromHttpRequest(request));

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotFoundException e) {
            logger.warn("Failed attempt to remove a skill from " +
                            "an engineer, from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
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

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('searchSwEngineer')")
    public List<SearchSwResponseDto> searchSwEngineer(@RequestBody SearchSwEngineerDto dto) {
        return _softwareEngineerService.searchSw(dto);
    }
    @PreAuthorize("hasAuthority('uploadCv')")
    @PostMapping(path = "/cv", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadCV(@RequestParam MultipartFile cv, Principal principal){
        try {
            String swEngineerEmail = principal.getName();
            Account acc = _accountService.findByEmail(swEngineerEmail);

            cvService.save(cv, acc.getEmployeeId());
            return new ResponseEntity("Cv uploaded", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity("Saving file failed", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity("Engineer not found", HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity getCvPdfResponse(UUID engineerId) throws IOException, NotFoundException {

        byte[] cvBytes = cvService.read(engineerId);

        // Create a ByteArrayResource from the byte array
        ByteArrayResource resource = new ByteArrayResource(cvBytes);

        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cv.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Return the response entity with the PDF file contents
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(cvBytes.length)
                .body(resource);
    }
    @PreAuthorize("hasAuthority('readCv')")
    @GetMapping(path = "/cv")
    public ResponseEntity<?> getCv(Principal principal){
        try {
            String swEngineerEmail = principal.getName();
            Account acc = _accountService.findByEmail(swEngineerEmail);

            return getCvPdfResponse(acc.getEmployeeId());

        }
        catch (NotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity( HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('readCv')")
    @GetMapping(path = "/cv/{id}")
    public ResponseEntity<?> getCvThirdParty(Principal principal, @PathVariable("id") UUID engineerId){
        try {
            String email = principal.getName();
            Account acc = _accountService.findByEmail(email);

            Authentication authentication = (Authentication) principal;
            List<String> authorities = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            var role = authorities.get(0);

            if(role.equals("ROLE_HR_MANAGER") || projectManagerService.isSuperior(acc.getEmployeeId(), engineerId))
            {
                    return getCvPdfResponse(engineerId);
            }

            return new ResponseEntity("access to this Cv is forbidden for current user", HttpStatus.FORBIDDEN);
        }
        catch (IOException e) {
            return new ResponseEntity("cv not found", HttpStatus.NOT_FOUND);
        } catch (NotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }
}
