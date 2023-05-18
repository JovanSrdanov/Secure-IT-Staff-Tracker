package jass.security.service.interfaces;

import jass.security.exception.EmailActivationExpiredException;
import jass.security.exception.NotFoundException;
import jass.security.model.AccountActivation;

import java.util.UUID;

public interface IAccountActivationService extends ICrudService<AccountActivation> {
    String createAcctivationLink(String email);

    void deleteById(UUID id);

    void activateAccount(UUID id) throws EmailActivationExpiredException, NotFoundException;

}
