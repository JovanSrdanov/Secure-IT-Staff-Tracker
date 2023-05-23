package jass.security.service.interfaces;

import jass.security.model.Privilege;

public interface IPrivilegeService extends ICrudService<Privilege> {
    Privilege findByName(String name);
}
