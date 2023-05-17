package jass.security.service.implementations;

import jass.security.model.Role;
import jass.security.repository.IRoleRepository;
import jass.security.service.interfaces.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@Primary
public class RoleService implements IRoleService {
    private final IRoleRepository _roleRepository;

    @Autowired
    public RoleService(IRoleRepository roleRepository) {
        this._roleRepository = roleRepository;
    }

    @Override
    public List<Role> findAll() {
        return null;
    }

    @Override
    public Role findById(UUID id) {
        return null;
    }

    @Override
    public Role save(Role entity) {
        entity.setId(UUID.randomUUID());
        return _roleRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Role findByName(String name) {
        return _roleRepository.findByName(name);
    }
}
