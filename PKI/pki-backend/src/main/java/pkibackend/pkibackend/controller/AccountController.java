package pkibackend.pkibackend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pkibackend.pkibackend.Utilities.ObjectMapperUtils;
import pkibackend.pkibackend.dto.AccountInfoDto;
import pkibackend.pkibackend.dto.BooleanResponse;
import pkibackend.pkibackend.dto.UpdatePasswordDto;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.service.interfaces.IAccountService;

import java.security.Principal;

@RestController
@RequestMapping("account")
public class
AccountController {
    private final IAccountService _accountService;

    @Autowired
    public AccountController(IAccountService accountService) {
        _accountService = accountService;
    }

    @PreAuthorize("hasRole('ROLE_CERTIFICATE_USER_CHANGE_PASSWORD')")
    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto, Principal principal) {
        try {
            _accountService.changePassword(principal.getName(), updatePasswordDto);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_PKI_ADMIN','ROLE_CERTIFICATE_USER')")
    @GetMapping("exists-by-email/{email}")
    public ResponseEntity<BooleanResponse> existsByEmail(@PathVariable("email") String email) {
        boolean exists = _accountService.existsByEmail(email);
        return new ResponseEntity<>(new BooleanResponse(exists), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CERTIFICATE_USER')")
    @GetMapping("allExceptLoggindIn")
    public ResponseEntity<?> findAllAdmin(Principal principal) {
        try {
            Account a = null;
            a = _accountService.findAccountByEmail(principal.getName());
            return new ResponseEntity<>(ObjectMapperUtils.mapAll(_accountService.findAllByIdIsNot(a.getId()), AccountInfoDto.class), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_PKI_ADMIN')")
    @GetMapping("all")
    public ResponseEntity<Iterable<AccountInfoDto>> findAllAdmin() {
        return new ResponseEntity<>(ObjectMapperUtils.mapAll(_accountService.findAllNotAdmin(), AccountInfoDto.class), HttpStatus.OK);
    }
}
