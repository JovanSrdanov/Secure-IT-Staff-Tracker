package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.dto.UpdatePasswordDto;
import pkibackend.pkibackend.model.Account;

public interface IAccountService extends ICrudService<Account> {
    Account findAccountByEmail(String email);


    void changePassword(String name, UpdatePasswordDto updatePasswordDto) throws Exception;
}
