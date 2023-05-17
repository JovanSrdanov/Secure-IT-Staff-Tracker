package jass.security.service.implementations;

import jass.security.dto.RegisterAccountDto;
import jass.security.exception.EmailTakenException;
import jass.security.exception.NotFoundException;
import jass.security.model.Account;
import jass.security.model.RegistrationRequestStatus;
import jass.security.model.Role;
import jass.security.repository.IAccountRepository;
import jass.security.repository.IRoleRepository;
import jass.security.service.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Primary
public class AccountService implements IAccountService {
    private final IAccountRepository _accountRepository;

    private final IRoleRepository _roleRespository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(IAccountRepository accountRepository, IRoleRepository roleRespository) {
        this._accountRepository = accountRepository;
        _roleRespository = roleRespository;
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
        if( entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        return _accountRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Account findByEmail(String email) {
        return _accountRepository.findByEmail(email);
    }

    @Override
    public UUID registerAccount(RegisterAccountDto dto) throws EmailTakenException {
        if(findByEmail(dto.getEmail()) != null) {
            throw new EmailTakenException();
        }

        Account newAcc = new Account();

        String salt = genereteSalt();

        newAcc.setEmail(dto.getEmail());
        newAcc.setPassword(passwordEncoder.encode(dto.getPassword() + salt));
        newAcc.setSalt(salt);
        newAcc.setId(UUID.randomUUID());
        //TODO Strahinja: Ovde treba id normalan ne random
        newAcc.setEmployeeId(UUID.randomUUID());
        newAcc.setStatus(RegistrationRequestStatus.PENDING);
        newAcc.setIsActivated(false);

        //TODO Strahinja: Da li ovo ovako ili nekako bolje da se salju ove role sa fronta?
        var role = _roleRespository.findByName(dto.getRole());
        var roles = new ArrayList<Role>();
        roles.add(role);

        newAcc.setRoles(roles);

        role.getUsers().add(newAcc);

        save(newAcc);

        _roleRespository.save(role);

        return newAcc.getId();
    }

    @Override
    public void approveAccount(String email, Boolean approve) throws NotFoundException {
        Account account = findByEmail(email);
        if(account == null) {
            throw new NotFoundException("Account not found");
        }
        if(approve) {
            account.setStatus(RegistrationRequestStatus.APPROVED);
        } else
            account.setStatus(RegistrationRequestStatus.REJECTED);

        save(account);
    }

    @Override
    public ArrayList<Account> findAllByStatus(RegistrationRequestStatus status) {
        var accs = _accountRepository.findAllByStatus(status);
        return accs;
    }

    private String genereteSalt() {
        int length = 8; // Desired length of the random string
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }


}
