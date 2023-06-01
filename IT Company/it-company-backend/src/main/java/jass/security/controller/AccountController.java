package jass.security.controller;

import jakarta.validation.Valid;
import jass.security.dto.ChangePasswordDto;
import jass.security.dto.RegisterEmployeeDto;
import jass.security.exception.NotFoundException;
import jass.security.exception.PasswordsDontMatchException;
import jass.security.model.Account;
import jass.security.model.RegistrationRequestStatus;
import jass.security.service.interfaces.IAccountRecoveryService;
import jass.security.service.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RestController
@RequestMapping("account")
public class AccountController {
    private final IAccountService _accountService;

    private final IAccountRecoveryService accountRecoveryService;

    @Autowired
    public AccountController(IAccountService _accountService, IAccountRecoveryService accountRecoveryService) {
        this._accountService = _accountService;
        this.accountRecoveryService = accountRecoveryService;
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

    @GetMapping("/block/{email}")
    @PreAuthorize("hasAuthority('blockAccount')")
    public ResponseEntity<?> blockAccount(@PathVariable String email) {
        try {
            _accountService.blockAccount(email);
            return ResponseEntity.ok("Account blocked!");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This account does not exist!");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDto dto) {
        try {
            _accountService.changePassword(dto);
            return ResponseEntity.ok("Password changed");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This account does not exist!");
        } catch (PasswordsDontMatchException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Passwords don`t match");
        }
    }

    @GetMapping("/reqest-recovery/{email}")
    public ResponseEntity<?> reqestRecvoery(@PathVariable String email) {
        try {
            return ResponseEntity.ok(accountRecoveryService.createRecoveryLink(email));
        } catch (NotFoundException | NoSuchAlgorithmException | InvalidKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Account does not exist or other error");
        }
    }


}
