package pkibackend.pkibackend.service.implementations;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pkibackend.pkibackend.model.Role;
import pkibackend.pkibackend.repository.RoleRepository;
import pkibackend.pkibackend.service.interfaces.IRoleService;

import java.util.List;

@Service
public class RoleService implements IRoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findById(Long id) {
        Role auth = this.roleRepository.getOne(id);
        return auth;
    }

    @Override
    public List<Role> findByName(String name) {
        List<Role> roles = this.roleRepository.findByName(name);
        return roles;
    }

}
