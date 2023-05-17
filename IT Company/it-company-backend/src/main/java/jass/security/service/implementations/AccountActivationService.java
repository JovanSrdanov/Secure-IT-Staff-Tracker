package jass.security.service.implementations;

import jass.security.exception.EmailActivationExpiredException;
import jass.security.exception.NotFoundException;
import jass.security.model.Account;
import jass.security.model.AccountActivation;
import jass.security.repository.IAccountActivationRepository;
import jass.security.repository.IAccountRepository;
import jass.security.service.interfaces.IAccountActivationService;
import jass.security.service.interfaces.IAccountService;
import jass.security.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Primary
public class AccountActivationService implements IAccountActivationService {

    private final IAccountActivationRepository accountActivationRepository;

    private final IAccountService accountService;

    @Autowired
    public AccountActivationService(IAccountActivationRepository accountActivationRepository, IAccountService accountRepository) {
        this.accountActivationRepository = accountActivationRepository;
        this.accountService = accountRepository;
    }

    @Override
    public List<AccountActivation> findAll() {
        return null;
    }

    @Override
    public AccountActivation findById(UUID id) {
        if(accountActivationRepository.findById(id).isPresent()) {
            return accountActivationRepository.findById(id).get();
        }
        return null;

    }

    @Override
    public AccountActivation save(AccountActivation entity) {
        return accountActivationRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public void deleteById(UUID id) {
        accountActivationRepository.deleteById(id);
    }

    @Override
    public void activateAccount(UUID id) throws EmailActivationExpiredException, NotFoundException {
        AccountActivation accountActivation = findById(id);
        if(accountActivation == null) {
            throw new NotFoundException("This activation does not exist");
        }
        if(accountActivation.getExpireyDate().before(new Date())) {
            deleteById(id);
            throw new EmailActivationExpiredException();
        }

        Account account = accountService.findByEmail(accountActivation.getEmail());
        account.setIsActivated(true);

        deleteById(id);
    }

    @Override
    public String createAcctivationLink(String email) {
        AccountActivation accountActivation = new AccountActivation();
        accountActivation.setId(UUID.randomUUID());
        accountActivation.setEmail(email);
        accountActivation.setExpireyDate(DateUtils.addHoursToDate(new Date(), 5));
        save(accountActivation);
        return "http://localhost:4761/auth/activate/" + accountActivation.getId().toString();
    }

}
