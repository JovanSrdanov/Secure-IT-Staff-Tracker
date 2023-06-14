package pkibackend.pkibackend.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pkibackend.pkibackend.dto.UpdatePasswordDto;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.exceptions.NotFoundException;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.model.Role;
import pkibackend.pkibackend.repository.AccountRepository;
import pkibackend.pkibackend.service.interfaces.IAccountService;
import pkibackend.pkibackend.service.interfaces.IRoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Primary
public class AccountService implements IAccountService {
    private final AccountRepository _accountRepository;
    private final PasswordEncoder _passwordEncoder;
    private final IRoleService _roleService;

    private final JavaMailSender _javaMailSender;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, RoleService roleService, JavaMailSender javaMailSender) {
        _accountRepository = accountRepository;
        _passwordEncoder = passwordEncoder;
        _roleService = roleService;
        _javaMailSender = javaMailSender;
    }

    @Override
    public Iterable<Account> findAll() {
        return _accountRepository.findAll();
    }

    @Override
    public List<Account> findAllNotAdmin() {
        List<Account> accounts = _accountRepository.findAll();
        List<Account> nonAdminAccounts = new ArrayList<>();
        for (Account account : accounts) {
            if (!isAccountAdmin(account)) {
                nonAdminAccounts.add(account);
            }
        }
        return nonAdminAccounts;
    }

    @Override
    public List<Account> findAllByIdIsNot(UUID accountId) {
        List<Account> accounts = _accountRepository.findAllByIdIsNot(accountId);
        List<Account> nonAdminAccounts = new ArrayList<>();
        for (Account account : accounts) {
            if (!isAccountAdmin(account)) {
                nonAdminAccounts.add(account);
            }
        }
        return nonAdminAccounts;
    }

    @Override
    public Account findById(UUID id) throws NotFoundException {
        if (_accountRepository.findById(id).isPresent()) {
            return _accountRepository.findById(id).get();
        }
        throw new NotFoundException("Account not found");
    }

    @Override
    public Account findByEmail(String email) throws NotFoundException {
        if (_accountRepository.findByEmail(email).isPresent()) {
            return _accountRepository.findByEmail(email).get();
        }

        throw new NotFoundException("Account with given email not found");
    }

    @Override
    public Boolean existsByEmail(String email) {
        return _accountRepository.findByEmail(email).isPresent();
    }

    @Override
    public Account save(Account entity) throws BadRequestException {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        return _accountRepository.save(entity);
    }

    @Override
    public Account updateAccount(Account updatedAccount, UUID accountId) throws BadRequestException, NotFoundException {
        Account oldAccount = this.findById(accountId);

        oldAccount.update(updatedAccount);

        return save(oldAccount);
    }

    @Override
    public void deleteById(UUID id) {
        _accountRepository.deleteById(id);
    }

    @Override
    public Account findAccountByEmail(String email) throws NotFoundException {
        if (_accountRepository.findByEmail(email).isPresent()) {

            return _accountRepository.findByEmail(email).get();
        }
        throw new NotFoundException("Account not found");
    }

    @Override
    public boolean isAccountAdmin(Account account) {
        for (Role role : account.getRoles()) {
            if (role.getName().equals("ROLE_PKI_ADMIN")) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void changePassword(String email, UpdatePasswordDto updatePasswordDto) throws Exception {
        Account account = findAccountByEmail(email);
        if (_passwordEncoder.matches(updatePasswordDto.getOldPassword() + account.getSalt(), account.getPassword())) {
            account.setPassword(_passwordEncoder.encode(updatePasswordDto.getNewPassword() + account.getSalt()));
            List<Role> roles = _roleService.findByName("ROLE_CERTIFICATE_USER");
            account.setRoles(roles);
            save(account);
            return;
        }
        throw new Exception("The old password is not correct.");

    }
}
