package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.dto.UpdatePasswordDto;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.model.Account;

import java.util.UUID;

public interface IAccountService extends ICrudService<Account> {
    Account findByEmail(String email);

    Account updateAccount(Account updatedAccount, UUID accountId) throws BadRequestException;

    Account findAccountByEmail(String email);

    void changePassword(String name, UpdatePasswordDto updatePasswordDto) throws Exception;
}
