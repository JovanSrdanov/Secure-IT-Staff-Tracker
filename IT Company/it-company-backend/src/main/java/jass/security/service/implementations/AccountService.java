package jass.security.service.implementations;

import jass.security.model.Account;
import jass.security.repository.IAccountRepository;
import jass.security.service.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class AccountService implements IAccountService {
    private final IAccountRepository _accountRepository;

    @Autowired
    public AccountService(IAccountRepository accountRepository) {
        this._accountRepository = accountRepository;
    }

    @Override
    public List<Account> findAll() {
        return _accountRepository.findAll();
    }

    @Override
    public Account findById(UUID id) {
        return null;
    }

    @Override
    public Account save(Account entity) {
        entity.setId(UUID.randomUUID());
        return _accountRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Account findByEmail(String email) {
        return _accountRepository.findByEmail(email);
    }
}
