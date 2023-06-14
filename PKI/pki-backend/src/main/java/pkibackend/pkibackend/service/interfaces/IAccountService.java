package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.dto.UpdatePasswordDto;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.exceptions.NotFoundException;
import pkibackend.pkibackend.model.Account;

import java.util.List;
import java.util.UUID;

public interface IAccountService extends ICrudService<Account> {
    Account findByEmail(String email) throws NotFoundException;

    Account updateAccount(Account updatedAccount, UUID accountId) throws BadRequestException, NotFoundException;

    Boolean existsByEmail(String email);

    List<Account> findAllByIdIsNot(UUID accountId);

    List<Account> findAllNotAdmin();

    void changePassword(String name, UpdatePasswordDto updatePasswordDto) throws Exception;

    Account findAccountByEmail(String email) throws NotFoundException;

    boolean isAccountAdmin(Account account);


}
