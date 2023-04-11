package pkibackend.pkibackend.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import pkibackend.pkibackend.dto.UpdatePasswordDto;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.model.Role;
import pkibackend.pkibackend.repository.AccountRepository;
import pkibackend.pkibackend.service.interfaces.IAccountService;
import pkibackend.pkibackend.service.interfaces.IRoleService;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class AccountService implements IAccountService {
    private final AccountRepository _accountRepository;
    private final PasswordEncoder _passwordEncoder;
    private final IRoleService _roleService;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        _accountRepository = accountRepository;
        _passwordEncoder = passwordEncoder;
        _roleService = roleService;
    }

    @Override
    public Iterable<Account> findAll() {
        return _accountRepository.findAll();
    }

    @Override
    public Account findById(UUID id) {
        if (_accountRepository.findById(id).isPresent()) {
            return _accountRepository.findById(id).get();
        }
        throw new NotFoundException("Account not found");
    }

    @Override
    public Account save(Account entity) throws BadRequestException {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        return _accountRepository.save(entity);
    }

    @Override
    public void deleteById(UUID id) {
        _accountRepository.deleteById(id);
    }

    @Override
    public Account findAccountByEmail(String email) {
        if (_accountRepository.findByEmail(email).isPresent()) {

            return _accountRepository.findByEmail(email).get();
        }
        throw new NotFoundException("Account not found");
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
