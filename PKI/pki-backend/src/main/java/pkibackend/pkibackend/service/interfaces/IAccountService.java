package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.model.Account;

public interface IAccountService extends ICrudService<Account>{
    Account findAccountByEmail(String email);

}
