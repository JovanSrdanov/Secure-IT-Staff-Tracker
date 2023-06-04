package jass.security.service.interfaces;
import jass.security.exception.EmailActivationExpiredException;
import jass.security.exception.NotFoundException;
import jass.security.model.AccountRecovery;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface IAccountRecoveryService extends ICrudService<AccountRecovery>{
    String createRecoveryLink(String mail) throws NotFoundException, NoSuchAlgorithmException, InvalidKeyException;
    void recoverAccount(String token, String newPassword) throws NotFoundException, EmailActivationExpiredException;

    AccountRecovery findByToken(String token);
}
