package jass.security.controller;

import jass.security.dto.PermissionUpdateRequest;
import jass.security.dto.PrivilegeInfoDto;
import jass.security.dto.RoleInfoDto;
import jass.security.service.interfaces.IPrivilegeService;
import jass.security.service.interfaces.IRoleService;
import jass.security.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("privilege")
public class PrivilegeController {
    private static final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);
    private final IPrivilegeService privilegeService;

    private final IRoleService roleService;

    @Autowired
    public PrivilegeController(IPrivilegeService privilegeService, IRoleService roleService) {
        this.privilegeService = privilegeService;
        this.roleService = roleService;
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('updatePrivilege')")
    public ResponseEntity<?> updatePrivileges(@RequestBody PermissionUpdateRequest dto) {
        roleService.updatePrivileges(dto);
        logger.info("Privileges for the role: " + dto.getRoleName() + "changed");

        return ResponseEntity.ok("Privileges updated");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('getAllPrivilege')")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(ObjectMapperUtils.mapAll(privilegeService.findAll(), PrivilegeInfoDto.class));
    }

    @GetMapping("/all/{roleName}")
    @PreAuthorize("hasAuthority('getAllPrivilegeForRole')")
    public ResponseEntity<?> getAllByName(@PathVariable String roleName) {
        return ResponseEntity.ok(ObjectMapperUtils.mapAll(roleService.findPrivileges(roleName), PrivilegeInfoDto.class));
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('getRoles')")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(ObjectMapperUtils.mapAll(roleService.findAll(), RoleInfoDto.class));
    }
}
