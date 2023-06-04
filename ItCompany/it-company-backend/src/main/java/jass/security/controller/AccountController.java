package jass.security.controller;

import jakarta.validation.Valid;
import jass.security.dto.ChangePasswordDto;
import jass.security.dto.RecoverAccountDto;
import jass.security.dto.RegisterEmployeeDto;
import jass.security.exception.EmailActivationExpiredException;
import jass.security.exception.NotFoundException;
import jass.security.exception.PasswordsDontMatchException;
import jass.security.model.Account;
import jass.security.model.RegistrationRequestStatus;
import jass.security.service.implementations.MailSenderService;
import jass.security.service.interfaces.IAccountRecoveryService;
import jass.security.service.interfaces.IAccountService;
import jass.security.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("account")
public class AccountController {
    private final IAccountService _accountService;

    private final IAccountRecoveryService accountRecoveryService;

    private final MailSenderService mailSenderService;

    @Autowired
    public AccountController(IAccountService _accountService, IAccountRecoveryService accountRecoveryService, MailSenderService mailSenderService) {
        this._accountService = _accountService;
        this.accountRecoveryService = accountRecoveryService;
        this.mailSenderService = mailSenderService;
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

    @GetMapping("/blockUnblockAccount/{email}")
    @PreAuthorize("hasAuthority('blockUnblockAccount')")
    public ResponseEntity<?> blockUnblockAccount(@PathVariable String email) {
        try {
            _accountService.blockUnblockAccount(email);
            return ResponseEntity.ok("Account blocked!");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This account does not exist!");
        }
    }


    @PostMapping("/change-password")
    @PreAuthorize("hasAuthority('changePassword')")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDto dto, Principal principal) {
        try {
            dto.setEmail(principal.getName());
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
            String link = accountRecoveryService.createRecoveryLink(email);
            String htmlLink = "Click this <a href=" + link + ">link</a> to recover account";
            mailSenderService.sendHtmlMail(email, "IT COMPANY", htmlLink);
            return ResponseEntity.ok("Email sent " + link);
        } catch (NotFoundException | NoSuchAlgorithmException | InvalidKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Account does not exist or other error with hashing");
        }
    }

    @PostMapping("/recover/{token}")
    public ResponseEntity<?> recoverAccount(@PathVariable String token, @RequestBody @Valid RecoverAccountDto dto) {
        try {
            accountRecoveryService.recoverAccount(token, dto.getNewPassword());
            return ResponseEntity.ok("New password set");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Account does not exist");
        } catch (EmailActivationExpiredException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Link expired");
        }
    }


}
