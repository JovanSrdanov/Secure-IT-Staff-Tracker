package jass.security.controller;

import jass.security.dto.PermissionUpdateRequest;
import jass.security.dto.PrivilegeInfoDto;
import jass.security.dto.RoleInfoDto;
import jass.security.service.interfaces.IPrivilegeService;
import jass.security.service.interfaces.IRoleService;
import jass.security.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("privilege")
public class PrivilegeController {
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

        return ResponseEntity.ok("Privileges updated");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('getPrivilege')")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(ObjectMapperUtils.mapAll(privilegeService.findAll(), PrivilegeInfoDto.class));
    }

    @GetMapping("/all/{roleName}")
    @PreAuthorize("hasAuthority('getPrivilege')")
    public ResponseEntity<?> getAllByName(@PathVariable String roleName) {
        return ResponseEntity.ok(ObjectMapperUtils.mapAll(roleService.findPrivileges(roleName), PrivilegeInfoDto.class));
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('getPrivilege')")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(ObjectMapperUtils.mapAll(roleService.findAll(), RoleInfoDto.class));
    }
 }
