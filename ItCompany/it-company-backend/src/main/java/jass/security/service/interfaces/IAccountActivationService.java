package jass.security.service.interfaces;

import jass.security.exception.EmailActivationExpiredException;
import jass.security.exception.NotFoundException;
import jass.security.model.AccountActivation;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public interface IAccountActivationService extends ICrudService<AccountActivation> {
    String createAcctivationLink(String email) throws NoSuchAlgorithmException, InvalidKeyException;

    void deleteById(UUID id);

    AccountActivation findByToken(String token);

    void activateAccount(String hash) throws EmailActivationExpiredException, NotFoundException;
}
