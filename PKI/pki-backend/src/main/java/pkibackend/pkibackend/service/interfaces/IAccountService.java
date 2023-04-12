package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.model.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAccountService extends ICrudService<Account>{
    Account findByEmail(String email);
    Account updateAccount(Account updatedAccount, UUID accountId) throws BadRequestException;
    public Boolean existsByEmail(String email);
    List<Account> findAllByIdIsNot(UUID accountId);
    List<Account> findAllNotAdmin();
}
