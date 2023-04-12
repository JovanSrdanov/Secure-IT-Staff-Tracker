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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pkibackend.pkibackend.dto.BooleanResponse;
import pkibackend.pkibackend.service.interfaces.IAccountService;

import java.math.BigInteger;
import pkibackend.pkibackend.Utilities.ObjectMapperUtils;
import pkibackend.pkibackend.dto.AccountInfoDto;
import pkibackend.pkibackend.dto.CertificateInfoDto;
import pkibackend.pkibackend.service.interfaces.IAccountService;

import java.util.UUID;

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
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("exists-by-email/{email}")
    public ResponseEntity<BooleanResponse> existsByEmail(@PathVariable("email") String email){
        boolean exists = _accountService.existsByEmail(email);
        return new ResponseEntity<>(new BooleanResponse(exists), HttpStatus.OK);
    }
    @GetMapping("allExceptLoggindIn/{accId}")
    public ResponseEntity<Iterable<AccountInfoDto>> findAllAdmin(@PathVariable UUID accId){
        return new ResponseEntity<>(ObjectMapperUtils.mapAll(_accountService.findAllByIdIsNot(accId), AccountInfoDto.class), HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<Iterable<AccountInfoDto>> findAllAdmin(){
        return new ResponseEntity<>(ObjectMapperUtils.mapAll(_accountService.findAllNotAdmin(), AccountInfoDto.class), HttpStatus.OK);
    }
}
