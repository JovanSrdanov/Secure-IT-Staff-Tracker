package jass.security.controller;

import jass.security.dto.RegisterEmployeeDto;
import jass.security.exception.BadRequestException;
import jass.security.model.Account;
import jass.security.service.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("account")
public class AccountController {
    private final IAccountService _accountService;

    @Autowired
    public AccountController(IAccountService _accountService) {
        this._accountService = _accountService;
    }

   @PostMapping("")
    public ResponseEntity createAccount(@RequestBody RegisterEmployeeDto account){
        Account newAccount = new Account();
        newAccount.setEmail(account.email());
        newAccount.setPassword(account.password());
        //newAccount.setRole(account.role());
        //DELETE
        newAccount.setEmployeeId(UUID.randomUUID());
        newAccount.setSalt("sol");
        _accountService.save(newAccount);
        return new ResponseEntity(HttpStatus.CREATED);
   }



}
