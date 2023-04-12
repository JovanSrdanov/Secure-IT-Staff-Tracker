package pkibackend.pkibackend.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.repository.AccountRepository;
import pkibackend.pkibackend.service.interfaces.IAccountService;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class AccountService implements IAccountService {
    private final AccountRepository _accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        _accountRepository = accountRepository;
    }

    @Override
    public Iterable<Account> findAll() {
        return _accountRepository.findAll();
    }

    @Override
    public List<Account> findAllNotAdmin() {
        return _accountRepository.findAll();
    }

    @Override
    public List<Account> findAllByIdIsNot(UUID accountId) {
        return _accountRepository.findAllByIdIsNot(accountId);
    }

    @Override
    public Account findById(UUID id) {
        if (_accountRepository.findById(id).isPresent()) {
            return _accountRepository.findById(id).get();
        }
        throw new NotFoundException("Account not found");
    }

    @Override
    public Account findByEmail(String email) {
        if (_accountRepository.findByEmail(email).isPresent()) {
            return _accountRepository.findByEmail(email).get();
        }

        throw new NotFoundException("Account with given email not found");
    }

    @Override
    public Account save(Account entity) throws BadRequestException {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        return _accountRepository.save(entity);
    }

    @Override
    public Account updateAccount(Account updatedAccount, UUID accountId) throws BadRequestException {
        Account oldAccount = this.findById(accountId);

        oldAccount.update(updatedAccount);

        return save(oldAccount);
    }

    @Override
    public void deleteById(UUID id) {
        _accountRepository.deleteById(id);
    }
}
