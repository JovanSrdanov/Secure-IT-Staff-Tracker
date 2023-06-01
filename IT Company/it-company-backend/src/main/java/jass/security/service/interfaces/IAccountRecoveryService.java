package jass.security.service.interfaces;
import jass.security.exception.NotFoundException;
import jass.security.model.AccountRecovery;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface IAccountRecoveryService extends ICrudService<AccountRecovery>{
    String createRecoveryLink(String mail) throws NotFoundException, NoSuchAlgorithmException, InvalidKeyException;
}
