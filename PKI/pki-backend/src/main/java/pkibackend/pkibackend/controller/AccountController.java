package pkibackend.pkibackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pkibackend.pkibackend.dto.UpdatePasswordDto;
import pkibackend.pkibackend.service.interfaces.IAccountService;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("account")
public class AccountController {
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
            return new ResponseEntity<>(true, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
