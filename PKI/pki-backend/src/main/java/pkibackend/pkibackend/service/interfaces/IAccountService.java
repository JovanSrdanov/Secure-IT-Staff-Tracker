package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface IAccountService extends ICrudService<Account>{
    public Account findByEmail(String email);
    Account updateAccount(Account updatedAccount, UUID accountId) throws BadRequestException;
    public Boolean existsByEmail(String email);
}
