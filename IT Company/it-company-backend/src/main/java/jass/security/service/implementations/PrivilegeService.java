package jass.security.service.implementations;

import jass.security.model.Privilege;
import jass.security.repository.IPrivilegeRepository;
import jass.security.repository.IRoleRepository;
import jass.security.service.interfaces.IPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public class PrivilegeService implements IPrivilegeService {
    private final IPrivilegeRepository _privilegeRepositroy;

    @Autowired
    public PrivilegeService(IPrivilegeRepository privilegeRepository) {
        this._privilegeRepositroy = privilegeRepository;
    }

    @Override
    public List<Privilege> findAll() {
        return null;
    }

    @Override
    public Privilege findById(UUID id) {
        return null;
    }

    @Override
    public Privilege save(Privilege entity) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Privilege findByName(String name) {
        return _privilegeRepositroy.findByName(name);
    }
}
