package jass.security.service.interfaces;

import jass.security.model.Privilege;
import jass.security.service.implementations.ICrudService;

public interface IPrivilegeService extends ICrudService<Privilege> {
    Privilege findByName(String name);
}
