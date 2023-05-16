package jass.security.service.interfaces;

import jass.security.model.Account;
import jass.security.service.implementations.ICrudService;

public interface IAccountService extends ICrudService<Account> {
    Account findByEmail(String email);
}
