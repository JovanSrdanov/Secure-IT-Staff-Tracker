package jass.security.service.implementations;

import jass.security.dto.PermissionUpdateRequest;
import jass.security.model.Privilege;
import jass.security.model.Role;
import jass.security.repository.IPrivilegeRepository;
import jass.security.repository.IRoleRepository;
import jass.security.service.interfaces.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
@Primary
public class RoleService implements IRoleService {
    private final IRoleRepository _roleRepository;

    private final IPrivilegeRepository _privilegeRepository;

    @Autowired
    public RoleService(IRoleRepository roleRepository, IPrivilegeRepository privilegeRepository) {
        this._roleRepository = roleRepository;
        _privilegeRepository = privilegeRepository;
    }

    @Override
    public List<Role> findAll() {
        return _roleRepository.findAll();
    }

    @Override
    public Role findById(UUID id) {
        return null;
    }

    @Override
    public Role save(Role entity) {
        if(entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        return _roleRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Role findByName(String name) {
        return _roleRepository.findByName(name);
    }

    @Override
    public void updatePrivileges(PermissionUpdateRequest dto) {
        Role role = findByName(dto.getRoleName());

        var privileges = dto.getPrivileges();
        var newPrivileges = new ArrayList<Privilege>();

        for(var privilege : privileges) {
           var privilegeToAdd = _privilegeRepository.findByName(privilege);
            newPrivileges.add(privilegeToAdd);
        }

        role.setPrivileges(newPrivileges);
        save(role);
    }

    @Override
    public ArrayList<Privilege> findPrivileges(String roleName) {
        return new ArrayList<>(findByName(roleName).getPrivileges());
    }
}
