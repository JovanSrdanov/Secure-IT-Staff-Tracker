package jass.security.service.interfaces;

import jass.security.dto.AccountApprovalDto;
import jass.security.dto.PermissionUpdateRequest;
import jass.security.model.Privilege;
import jass.security.model.Role;
import jass.security.service.implementations.ICrudService;

import java.util.ArrayList;

public interface IRoleService extends ICrudService<Role> {
    Role findByName(String name);

    void updatePrivileges(PermissionUpdateRequest dto);

    ArrayList<Privilege> findPrivileges(String roleName);
}
