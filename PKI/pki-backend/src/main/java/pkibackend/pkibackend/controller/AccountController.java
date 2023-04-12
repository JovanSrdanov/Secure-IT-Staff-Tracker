package pkibackend.pkibackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("allExceptLoggindIn/{accId}")
    public ResponseEntity<Iterable<AccountInfoDto>> findAllAdmin(@PathVariable UUID accId){
        return new ResponseEntity<>(ObjectMapperUtils.mapAll(_accountService.findAllByIdIsNot(accId), AccountInfoDto.class), HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<Iterable<AccountInfoDto>> findAllAdmin(){
        return new ResponseEntity<>(ObjectMapperUtils.mapAll(_accountService.findAllNotAdmin(), AccountInfoDto.class), HttpStatus.OK);
    }
}
