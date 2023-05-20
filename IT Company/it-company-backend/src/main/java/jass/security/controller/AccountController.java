package jass.security.controller;

import jass.security.dto.AccountApprovalDto;
import jass.security.dto.RegisterEmployeeDto;
import jass.security.model.Account;
import jass.security.model.RegistrationRequestStatus;
import jass.security.service.interfaces.IAccountService;
import jass.security.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity createAccount(@RequestBody RegisterEmployeeDto account) {
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


    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('allPendingApproval')")
    public ResponseEntity<?> findAllPendingApproval() {
        return ResponseEntity.ok(_accountService.findAllByStatusInfo(RegistrationRequestStatus.PENDING));
    }


}
