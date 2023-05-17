package jass.security.service.interfaces;

import jass.security.model.Privilege;
import jass.security.service.implementations.ICrudService;

import java.util.ArrayList;

public interface IPrivilegeService extends ICrudService<Privilege> {
    Privilege findByName(String name);
}
