package jass.security.service.implementations;

import jass.security.exception.NotFoundException;
import jass.security.model.Account;
import jass.security.model.AccountActivation;
import jass.security.model.AccountRecovery;
import jass.security.repository.IAccountRecoveryRepository;
import jass.security.service.interfaces.IAccountRecoveryService;
import jass.security.service.interfaces.IAccountService;
import jass.security.utils.DateUtils;
import jass.security.utils.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Primary
public class AccountRecoveryService implements IAccountRecoveryService {
    private final IAccountRecoveryRepository accountRecoveryRepository;

    private final IAccountService accountService;

    @Value("${hmacSecret}")
    private String hmacSecret;

    @Autowired
    public AccountRecoveryService(IAccountRecoveryRepository accountRecoveryRepository, IAccountService accountService) {
        this.accountRecoveryRepository = accountRecoveryRepository;
        this.accountService = accountService;
    }

    @Override
    public List<AccountRecovery> findAll() {
        return null;
    }

    @Override
    public AccountRecovery findById(UUID id) throws NotFoundException {
        if(accountRecoveryRepository.findById(id).isPresent())
            return accountRecoveryRepository.findById(id).get();
        else
            throw new NotFoundException("This account does not exist");
    }

    @Override
    public AccountRecovery save(AccountRecovery entity) {
        return accountRecoveryRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        accountRecoveryRepository.deleteById(id);
    }

    @Override
    public String createRecoveryLink(String email) throws NotFoundException, NoSuchAlgorithmException, InvalidKeyException {
        Account account = accountService.findByEmail(email);

        AccountRecovery accountRecovery = new AccountRecovery();
        accountRecovery.setId(UUID.randomUUID());
        accountRecovery.setEmail(email);
        accountRecovery.setExpireyDate(DateUtils.addHoursToDate(new Date(), 5));

        String hash = HashUtils.hmacWithJava("HmacSHA256", accountRecovery.getId().toString(), hmacSecret);
        accountRecovery.setToken(hash);

        save(accountRecovery);

        return "https://localhost:4430/account/recover/" + hash;
    }
}
