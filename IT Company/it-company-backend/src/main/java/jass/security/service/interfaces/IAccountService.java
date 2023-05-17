package jass.security.service.interfaces;

import jass.security.dto.RegisterAccountDto;
import jass.security.exception.EmailTakenException;
import jass.security.model.Account;
import jass.security.service.implementations.ICrudService;

import java.util.UUID;

public interface IAccountService extends ICrudService<Account> {
    Account findByEmail(String email);
    UUID registerAccount(RegisterAccountDto dto) throws EmailTakenException;
}
