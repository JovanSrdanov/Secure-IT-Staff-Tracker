package jass.security.controller;

import jass.security.dto.swengineer.AddSkillDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("sw-engineer")
public class SoftwareEngineerController {
    @PostMapping("skill")
    @PreAuthorize("hasAuthority('addSkillSwEngineer')")
    public ResponseEntity<?> AddSkill(@RequestBody AddSkillDto dto, Principal principal){
        return null;
    }

    @GetMapping("skill/{id}")
    @PreAuthorize("hasAuthority('removeSkillSwEngineer')")
    public ResponseEntity<?> RemoveSkill(@PathVariable("id") UUID skillId, Principal principal){
        return null;
    }
}
