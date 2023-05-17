package jass.security.service.interfaces;

import jass.security.dto.RegisterAccountDto;
import jass.security.exception.EmailTakenException;
import jass.security.model.Account;
import jass.security.model.RegistrationRequestStatus;
import jass.security.service.implementations.ICrudService;

import java.util.ArrayList;
import java.util.UUID;

public interface IAccountService extends ICrudService<Account> {
    Account findByEmail(String email);
    UUID registerAccount(RegisterAccountDto dto) throws EmailTakenException;

    void approveAccount(String email, Boolean approve);

    ArrayList<Account> findAllByStatus(RegistrationRequestStatus status);
}
