package jass.security.service.interfaces;

import jass.security.model.Role;
import jass.security.service.implementations.ICrudService;

public interface IRoleService extends ICrudService<Role> {
    Role findByName(String name);
}
